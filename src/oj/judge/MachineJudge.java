package oj.judge;

import oj.core.JudgeResult;
import oj.core.Status;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MachineJudge {
    // 专门捕获 C++ 输出的正则雷达
    private static final Pattern JSON_REGEX = Pattern.compile(
        "\\{\"status\":\"(\\w+)\",\"passed\":(\\d+),\"total\":(\\d+),\"time_ms\":(\\d+),\"mem_kb\":(\\d+),\"detail\":\"((?:\\\\.|[^\"])*)\"\\}"
    );

    private final String judgeBinary;

    public MachineJudge(String judgeBinary) {
        this.judgeBinary = judgeBinary;
    }

    public JudgeResult judge(String problemDir, String srcFile, String lang, long timeMs, int memMb) {
        // 利用 ProcessBuilder 跨界创建 Linux 子进程
        ProcessBuilder pb = new ProcessBuilder(
            judgeBinary,
            "--problem", problemDir,
            "--src", srcFile,
            "--lang", lang,
            "--time-ms", String.valueOf(timeMs),
            "--mem-mb", String.valueOf(memMb)
        );
        pb.directory(new File("."));

        try {
            Process proc = pb.start(); // 激活 C++ 判题机进程

            boolean exited = proc.waitFor(timeMs + 5000, TimeUnit.MILLISECONDS);
            if (!exited) {
                proc.destroyForcibly(); // 彻底强杀子进程
                proc.waitFor();
                return new JudgeResult(Status.TLE, 0, 0, "Java 侧强制终止超时进程", timeMs);
            }

            String stdout = readAll(proc.getInputStream());
            String stderr = readAll(proc.getErrorStream());
            String jsonLine = lastNonBlankLine(stdout);

            if (jsonLine.isBlank()) {
                String detail = stderr.isBlank() ? "C++ 沙箱没有任何标准输出" : "C++ 沙箱无 JSON 输出: " + stderr.trim();
                return new JudgeResult(Status.RE, 0, 0, detail, 0);
            }

            // 正则切分提取
            Matcher m = JSON_REGEX.matcher(jsonLine);
            if (!m.find()) {
                return new JudgeResult(Status.RE, 0, 0, "无法解析的沙箱数据: " + jsonLine, 0);
            }

            Status status = mapStatus(m.group(1));
            int passed = Integer.parseInt(m.group(2));
            int total = Integer.parseInt(m.group(3));
            long actualTime = Long.parseLong(m.group(4));
            int memKb = Integer.parseInt(m.group(5));
            String detail = unescapeJson(m.group(6));

            return new JudgeResult(status, passed, total, detail + " (Memory: " + memKb + "KB)", actualTime);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new JudgeResult(Status.RE, 0, 0, "调用沙箱时 Java 线程被中断", 0);
        } catch (Exception e) {
            return new JudgeResult(Status.RE, 0, 0, "调用沙箱时 Java 发生异常: " + e.getMessage(), 0);
        }
    }

    private String readAll(InputStream in) throws IOException {
        return new String(in.readAllBytes(), StandardCharsets.UTF_8);
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
}
