package oj;

import oj.core.JudgeResult;
import oj.core.Problem;
import oj.core.ProblemMeta;
import oj.core.Status;
import oj.core.Submission;
import oj.db.Db;
import oj.db.ProblemDao;
import oj.db.SubmissionDao;
import oj.io.ProblemLoader;
import oj.judge.MachineJudge;
import oj.service.ProblemService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    private static final String USERNAME = "student";
    private static final int MEMORY_LIMIT_MB = 256;
    private static final int HISTORY_LIMIT = 100;

    public static void main(String[] args) {
        ProblemService problemService = new ProblemService(
            new ProblemDao(), new ProblemLoader("problems"));
        SubmissionDao submissionDao = new SubmissionDao();
        MachineJudge machineJudge = new MachineJudge("judge/judge");

        try {
            Map<Integer, ProblemMeta> available = problemService.listMeta();
            List<Integer> problemIds = selectProblemIds(args, available);

            System.out.println("== Mini-OJ M5b: Database + Filesystem Demo ==");
            System.out.printf("[INFO] Problems in database: %d%n", available.size());
            System.out.printf("[INFO] Problems selected: %s%n%n", problemIds);

            for (int problemId : problemIds) {
                runSample(
                    problemId, problemService, submissionDao, machineJudge);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Mini-OJ demo failed: " + e.getMessage());
            e.printStackTrace(System.err);
        } finally {
            Db.close();
        }
    }

    private static List<Integer> selectProblemIds(
            String[] args, Map<Integer, ProblemMeta> available) {
        if (args.length == 0) {
            return new ArrayList<>(available.keySet());
        }

        List<Integer> selected = new ArrayList<>();
        Arrays.stream(args).forEach(value -> {
            try {
                int id = Integer.parseInt(value);
                if (available.containsKey(id)) {
                    selected.add(id);
                } else {
                    System.err.printf("[WARN] Problem %d is not in the database.%n", id);
                }
            } catch (NumberFormatException e) {
                System.err.printf("[WARN] Ignoring invalid problem ID: %s%n", value);
            }
        });
        return selected;
    }

    private static void runSample(
            int problemId,
            ProblemService problemService,
            SubmissionDao submissionDao,
            MachineJudge machineJudge) {
        System.out.printf("-- Problem %d --%n", problemId);
        try {
            Problem problem = problemService.load(problemId);
            Path source = Paths.get("samples", "solutions", problemId + ".cpp");

            System.out.printf("[INFO] Title: %s%n", problem.getTitle());
            System.out.printf("[INFO] Test cases: %d%n", problem.getCases().size());
            System.out.printf("[INFO] Time limit: %d ms%n", problem.getTimeLimitMs());

            if (problem.getCases().isEmpty()) {
                System.out.println("[SKIP] No filesystem test cases were found.");
                System.out.println();
                return;
            }
            if (!source.toFile().isFile()) {
                System.out.printf("[SKIP] Sample solution not found: %s%n%n", source);
                return;
            }

            JudgeResult result = machineJudge.judge(
                "problems/" + problemId,
                source.toAbsolutePath().toString(),
                "cpp",
                problem.getTimeLimitMs(),
                MEMORY_LIMIT_MB
            );

            Submission submission = new Submission(
                problemId, USERNAME, "cpp", source.toAbsolutePath().toString());
            submission.setResult(result);
            submissionDao.save(submission);

            System.out.println("[RESULT] " + result);
            System.out.println("[INFO] Submission saved to MySQL.");
            printHistory(problemId, submissionDao);
        } catch (Exception e) {
            System.err.printf("[ERROR] Problem %d failed: %s%n", problemId, e.getMessage());
        }
        System.out.println();
    }

    private static void printHistory(int problemId, SubmissionDao submissionDao)
            throws Exception {
        List<Submission> history =
            submissionDao.listByUser(USERNAME, problemId, HISTORY_LIMIT);
        Map<Status, Long> stats = history.stream()
            .filter(submission -> submission.getResult() != null)
            .collect(Collectors.groupingBy(
                submission -> submission.getResult().getStatus(),
                Collectors.counting()
            ));

        System.out.printf("[HISTORY] User=%s, submissions=%d%n", USERNAME, history.size());
        System.out.printf("%-8s %7s  %s%n", "Status", "Count", "Meaning");
        System.out.printf("%-8s %7s  %s%n", "------", "-----", "-------");
        for (Status status : Status.values()) {
            long count = stats.getOrDefault(status, 0L);
            if (count > 0) {
                System.out.printf(
                    "%-8s %7d  %s%n", status, count, describeStatus(status));
            }
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
