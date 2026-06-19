package oj.judge.queue;

import oj.core.JudgeResult;
import oj.core.JudgeTask;
import oj.core.Status;
import oj.core.Submission;
import oj.db.SubmissionDao;
import oj.judge.MachineJudge;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JudgeWorker implements Runnable {
    private static final String PROBLEMS_DIR = "problems";
    private static final String SUBMISSIONS_DIR = "submissions";

    private final JudgeQueue<JudgeTask> queue;
    private final SubmissionDao submissionDao;
    private final MachineJudge machineJudge;

    public JudgeWorker(
            JudgeQueue<JudgeTask> queue,
            SubmissionDao submissionDao,
            MachineJudge machineJudge) {
        this.queue = queue;
        this.submissionDao = submissionDao;
        this.machineJudge = machineJudge;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            JudgeTask task = null;
            try {
                task = queue.take();
                JudgeResult result = evaluate(task);

                Submission submission = new Submission(
                    task.getProblemId(),
                    task.getUserName(),
                    task.getLang(),
                    sourcePathFor(task).toAbsolutePath().toString()
                );
                submission.setResult(result);
                submissionDao.save(submission);
                task.complete(result);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                if (task != null) {
                    task.complete(new JudgeResult(
                        Status.RE,
                        0,
                        0,
                        "Worker error: " + safeMessage(e),
                        0L
                    ));
                }
            }
        }
    }

    private JudgeResult evaluate(JudgeTask task) throws Exception {
        Path sourcePath = sourcePathFor(task);
        Files.createDirectories(sourcePath.getParent());
        Files.write(sourcePath, task.getSource());

        return machineJudge.judge(
            PROBLEMS_DIR + "/" + task.getProblemId(),
            sourcePath.toString(),
            task.getLang(),
            task.getTimeLimitMs(),
            task.getMemoryLimitMb()
        );
    }

    private Path sourcePathFor(JudgeTask task) {
        return Paths.get(
            SUBMISSIONS_DIR,
            "sub" + task.getSubmissionId() + extensionFor(task.getLang())
        );
    }

    private String extensionFor(String language) {
        switch (language.toLowerCase()) {
            case "java":
                return ".java";
            case "python":
                return ".py";
            case "c":
                return ".c";
            default:
                return ".cpp";
        }
    }

    private String safeMessage(Exception e) {
        return e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
    }
}
