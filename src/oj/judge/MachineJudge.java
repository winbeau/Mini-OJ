package oj.judge;

import oj.core.JudgeResult;
import oj.core.Status;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MachineJudge {
    private static final Pattern JSON_REGEX = Pattern.compile(
        "\\{\"status\":\"(\\w+)\",\"passed\":(\\d+),\"total\":(\\d+),\"time_ms\":(\\d+),\"mem_kb\":(\\d+),\"detail\":\"((?:\\\\.|[^\"])*)\"\\}"
    );

    private final String judgeBinary;

    public MachineJudge(String judgeBinary) {
        this.judgeBinary = judgeBinary;
    }

    public JudgeResult judge(String problemDir, String srcFile, String lang, long timeMs, int memMb) {
        ProcessBuilder pb = new ProcessBuilder(
            judgeBinary,
            "--problem", problemDir,
            "--src", srcFile,
            "--lang", lang,
            "--time-ms", String.valueOf(timeMs),
            "--mem-mb", String.valueOf(memMb)
        );
        pb.directory(new File("."));

        Process proc = null;
        try {
            proc = pb.start();
            StreamCollector stdoutCollector =
                new StreamCollector(proc.getInputStream(), "judge-stdout");
            StreamCollector stderrCollector =
                new StreamCollector(proc.getErrorStream(), "judge-stderr");
            stdoutCollector.start();
            stderrCollector.start();

            proc.waitFor();
            stdoutCollector.join();
            stderrCollector.join();

            String stdout = stdoutCollector.content();
            String stderr = stderrCollector.content();
            String jsonLine = lastNonBlankLine(stdout);

            if (jsonLine.isBlank()) {
                String detail = stderr.isBlank()
                    ? "Judge produced no output"
                    : "Judge produced no JSON output: " + stderr.trim();
                return new JudgeResult(Status.RE, 0, 0, detail, 0);
            }

            Matcher m = JSON_REGEX.matcher(jsonLine);
            if (!m.find()) {
                return new JudgeResult(Status.RE, 0, 0, "Unparseable judge output: " + jsonLine, 0);
            }

            Status status = mapStatus(m.group(1));
            int passed = Integer.parseInt(m.group(2));
            int total = Integer.parseInt(m.group(3));
            long actualTime = Long.parseLong(m.group(4));
            int memKb = Integer.parseInt(m.group(5));
            String detail = unescapeJson(m.group(6));

            return new JudgeResult(status, passed, total, detail + " (Memory: " + memKb + "KB)", actualTime);

        } catch (InterruptedException e) {
            if (proc != null) {
                proc.destroyForcibly();
            }
            Thread.currentThread().interrupt();
            return new JudgeResult(Status.RE, 0, 0, "Judge call was interrupted", 0);
        } catch (Exception e) {
            return new JudgeResult(Status.RE, 0, 0, "Judge call failed: " + e.getMessage(), 0);
        }
    }

    private String lastNonBlankLine(String text) {
        String[] lines = text.split("\\R");
        for (int i = lines.length - 1; i >= 0; i--) {
            String line = lines[i].trim();
            if (!line.isEmpty()) return line;
        }
        return "";
    }

    private Status mapStatus(String raw) {
        switch (raw.toUpperCase()) {
            case "AC":  return Status.AC;
            case "WA":  return Status.WA;
            case "TLE": return Status.TLE;
            case "MLE": return Status.MLE;
            case "RE":  return Status.RE;
            case "CE":  return Status.CE;
            case "PE":  return Status.PE;
            case "ERR": return Status.RE;
            default:    return Status.RE;
        }
    }

    private String unescapeJson(String value) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c != '\\' || i + 1 >= value.length()) {
                out.append(c);
                continue;
            }

            char next = value.charAt(++i);
            switch (next) {
                case '"':  out.append('"'); break;
                case '\\': out.append('\\'); break;
                case 'n':  out.append('\n'); break;
                case 'r':  out.append('\r'); break;
                case 't':  out.append('\t'); break;
                default:   out.append(next); break;
            }
        }
        return out.toString();
    }

    private static final class StreamCollector implements Runnable {
        private final InputStream input;
        private final ByteArrayOutputStream output = new ByteArrayOutputStream();
        private final Thread thread;
        private IOException failure;

        private StreamCollector(InputStream input, String threadName) {
            this.input = input;
            this.thread = new Thread(this, threadName);
            this.thread.setDaemon(true);
        }

        private void start() {
            thread.start();
        }

        private void join() throws InterruptedException, IOException {
            thread.join();
            if (failure != null) {
                throw failure;
            }
        }

        private String content() {
            return output.toString(StandardCharsets.UTF_8);
        }

        @Override
        public void run() {
            try (InputStream stream = input) {
                stream.transferTo(output);
            } catch (IOException e) {
                failure = e;
            }
        }
    }
}
