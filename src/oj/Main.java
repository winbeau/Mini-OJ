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
            ProblemRepository repo = new ProblemRepository("problems");
            MachineJudge machine = new MachineJudge("judge/judge");
            SubmissionStore store = new SubmissionStore("submissions.dat");

            Problem problem = repo.get(1001);
            if (problem == null) {
                System.out.println("[ERROR] Problem 1001 was not found.");
                return;
            }

            System.out.println("== Mini-OJ M5a: C++ Judge Demo ==");
            System.out.printf("[INFO] Loaded problem %d: %s (%d test cases)%n",
                problem.getId(), problem.getTitle(), problem.getCases().size());

            File subDir = new File("submissions");
            if (!subDir.exists()) subDir.mkdir();
            File mockSrc = new File(subDir, "submit_1001.cpp");
            
            String mockCode = "#include <iostream>\nusing namespace std;\nint main() {\n" +
                              "    int a, b;\n    while(cin >> a >> b) { cout << (a + b) << endl; }\n" +
                              "    return 0;\n}";
            Files.write(mockSrc.toPath(), mockCode.getBytes());

            System.out.printf("[INFO] Sample submission written to %s%n", mockSrc.getPath());
            System.out.println("[INFO] Running external C++ judge...");
            JudgeResult result = machine.judge("problems/1001", mockSrc.getAbsolutePath(), "cpp", problem.getTimeLimitMs(), 256);

            Submission sub = new Submission(problem.getId(), "ZhaoWenbiao", "cpp", mockSrc.getAbsolutePath());
            sub.setResult(result);
            store.save(sub);

            System.out.println("[RESULT] " + result);
            System.out.println("[INFO] Submission result saved to history.");

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
            default:  return "Unknown status";
        }
    }
}
