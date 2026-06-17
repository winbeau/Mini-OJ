package oj.io;

import oj.core.ProblemMeta;
import java.io.*;

public class ConfigFile {
    public static ProblemMeta read(File dir) throws IOException {
        File cfg = new File(dir, "config.txt");
        String title = "";
        String judgeClass = "oj.judge.StandardJudge";
        long timeLimitMs = 1000L;
        
        try (BufferedReader br = new BufferedReader(new FileReader(cfg))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("=", 2);
                if (parts.length < 2) continue;

                String key = parts[0].trim();
                String value = parts[1].trim();

                switch (key) {
                    case "title": title = value; break;
                    case "judge": judgeClass = value; break;
                    case "timeLimitMs": timeLimitMs = Long.parseLong(value); break;
                }
            }
        }
        return new ProblemMeta(title, judgeClass, timeLimitMs);
    }
}
