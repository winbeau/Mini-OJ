package oj;

import oj.core.*;
import oj.io.*;
import oj.judge.MachineJudge;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. 初始化仓储和核心工具
            ProblemRepository repo = new ProblemRepository("problems");
            MachineJudge machine = new MachineJudge("judge/judge");
            SubmissionStore store = new SubmissionStore("submissions.dat");

            // 2. 加载 1001 号题（如果存在）
            Problem problem = repo.get(1001);
            if (problem == null) {
                System.out.println("[ERROR] Problem 1001 was not found.");
                return;
            }

            System.out.println("== Mini-OJ M5a: C++ Judge Demo ==");
            System.out.printf("[INFO] Loaded problem %d: %s (%d test cases)%n",
                problem.getId(), problem.getTitle(), problem.getCases().size());

            // 3. 模拟选手提交：生成一个临时的选手代码文件落地
            File subDir = new File("submissions");
            if (!subDir.exists()) subDir.mkdir();
            File mockSrc = new File(subDir, "submit_1001.cpp");
            
            // 写入一段故意的死循环或错题 C++ 代码来进行工业极限流测试
            String mockCode = "#include <iostream>\nusing namespace std;\nint main() {\n" +
                              "    int a, b;\n    while(cin >> a >> b) { cout << (a + b) << endl; }\n" +
                              "    return 0;\n}";
            Files.write(mockSrc.toPath(), mockCode.getBytes());

            // 4. 调用第三代外部 C++ 判题代理
            System.out.printf("[INFO] Sample submission written to %s%n", mockSrc.getPath());
            System.out.println("[INFO] Running external C++ judge...");
            JudgeResult result = machine.judge("problems/1001", mockSrc.getAbsolutePath(), "cpp", problem.getTimeLimitMs(), 256);
            
            // 5. 封装 Submission 并追加序列化落盘
            Submission sub = new Submission(problem.getId(), "ZhaoWenbiao", "cpp", mockSrc.getAbsolutePath());
            sub.setResult(result);
            store.save(sub);

            System.out.println("[RESULT] " + result);
            System.out.println("[INFO] Submission result saved to history.");

            // 6. Ch15 高阶技巧：全量读取历史记录，使用 Stream 流进行大数据分类统计！
            System.out.println();
            System.out.println("== Submission History Summary ==");
            List<Submission> history = store.loadAll();
            
            Map<Status, Long> stats = history.stream()
                .collect(Collectors.groupingBy(s -> s.getResult().getStatus(), Collectors.counting()));

            System.out.printf("[INFO] History file: %s%n", "submissions.dat");
            System.out.printf("[INFO] Total saved submissions: %d%n%n", history.size());
            System.out.printf("%-8s %12s  %s%n", "Status", "Count", "Meaning");
            System.out.printf("%-8s %12s  %s%n", "------", "-----", "-------");
            for (Status status : Status.values()) {
                long count = stats.getOrDefault(status, 0L);
                if (count > 0) {
                    System.out.printf("%-8s %12d  %s%n",
                        status, count, describeStatus(status));
                }
            }

        } catch (Exception e) {
            System.err.println("[ERROR] Mini-OJ demo failed: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    private static String describeStatus(Status status) {
        switch (status) {
            case AC:  return "Accepted submissions";
            case WA:  return "Wrong answer submissions";
            case TLE: return "Time limit exceeded submissions";
            case MLE: return "Memory limit exceeded submissions";
            case RE:  return "Runtime error submissions";
            case CE:  return "Compile error submissions";
            case PE:  return "Presentation error submissions";
            case ERR: return "Judge internal error submissions";
            default:  return "Unknown status";
        }
    }
}
