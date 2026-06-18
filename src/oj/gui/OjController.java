package oj.gui;

import oj.core.JudgeResult;
import oj.core.ProblemMeta;
import oj.core.Submission;
import oj.db.SubmissionDao;
import oj.judge.MachineJudge;
import oj.service.ProblemService;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class OjController {
    private static final String PROBLEMS_DIR = "problems";
    private static final String SUBMISSIONS_DIR = "submissions";
    private static final String USER_NAME = "student";
    private static final int MEMORY_LIMIT_MB = 256;

    private final OjFrame view;
    private final ProblemService problemService;
    private final MachineJudge machineJudge;
    private final SubmissionDao submissionDao;

    public OjController(
            OjFrame view,
            ProblemService problemService,
            MachineJudge machineJudge,
            SubmissionDao submissionDao) {
        this.view = view;
        this.problemService = problemService;
        this.machineJudge = machineJudge;
        this.submissionDao = submissionDao;

        initProblems();
        bindEvents();
    }

    private void initProblems() {
        try {
            Map<Integer, ProblemMeta> all = problemService.listMeta();
            List<Integer> ids = new ArrayList<>(all.keySet());
            List<String> titles = new ArrayList<>();
            ids.forEach(id -> titles.add(all.get(id).getTitle()));
            view.loadProblems(ids, titles);

            if (ids.isEmpty()) {
                view.getSubmitBtn().setEnabled(false);
                view.getResultLabel().setText("No problems available");
            }
        } catch (Exception e) {
            view.getSubmitBtn().setEnabled(false);
            view.getResultLabel().setText("Problem loading failed");
            JOptionPane.showMessageDialog(
                view,
                "Failed to load problems: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void bindEvents() {
        view.getSubmitBtn().addActionListener(event -> onSubmit());
    }

    void onSubmit() {
        int problemId = view.getSelectedProblemId();
        if (problemId < 0) {
            JOptionPane.showMessageDialog(
                view,
                "Please select a problem.",
                "Submission",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String language = view.getSelectedLang();
        String sourceCode = view.getSourceCode();
        if (language == null) {
            JOptionPane.showMessageDialog(
                view,
                "Please select a language.",
                "Submission",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (sourceCode.isBlank()) {
            JOptionPane.showMessageDialog(
                view,
                "Source code cannot be empty.",
                "Submission",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        view.getSubmitBtn().setEnabled(false);
        view.getResultLabel().setText("Judging...");

        SwingWorker<SubmissionOutcome, Void> worker =
            new SwingWorker<SubmissionOutcome, Void>() {
                @Override
                protected SubmissionOutcome doInBackground() throws Exception {
                    Path sourceFile = writeSourceFile(language, sourceCode);
                    ProblemMeta meta = problemService.meta(problemId);
                    JudgeResult result = machineJudge.judge(
                        PROBLEMS_DIR + "/" + problemId,
                        sourceFile.toString(),
                        language,
                        meta.getTimeLimitMs(),
                        MEMORY_LIMIT_MB
                    );

                    Submission submission = new Submission(
                        problemId,
                        USER_NAME,
                        language,
                        sourceFile.toAbsolutePath().toString()
                    );
                    submission.setResult(result);
                    submissionDao.save(submission);
                    return new SubmissionOutcome(submission, result);
                }

                @Override
                protected void done() {
                    view.getSubmitBtn().setEnabled(true);
                    try {
                        SubmissionOutcome outcome = get();
                        showResult(outcome.submission, outcome.result);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        showFailure("Submission was interrupted.");
                    } catch (ExecutionException e) {
                        Throwable cause = e.getCause();
                        String message = cause == null ? e.getMessage() : cause.getMessage();
                        showFailure("Submission failed: " + message);
                    }
                }
            };
        worker.execute();
    }

    private Path writeSourceFile(String language, String sourceCode) throws Exception {
        Path directory = Paths.get(SUBMISSIONS_DIR);
        Files.createDirectories(directory);
        Path sourceFile = Files.createTempFile(
            directory,
            "submission-",
            extensionFor(language)
        );
        Files.writeString(sourceFile, sourceCode, StandardCharsets.UTF_8);
        return sourceFile;
    }

    private String extensionFor(String language) {
        switch (language.toLowerCase()) {
            case "python":
                return ".py";
            case "java":
                return ".java";
            case "c":
                return ".c";
            default:
                return ".cpp";
        }
    }

    private void showResult(Submission submission, JudgeResult result) {
        view.getResultLabel().setText(result.toString());
        view.appendHistory(
            submission.getId(),
            submission.getProblemId(),
            submission.getLang(),
            result.getStatus().name(),
            result.getPassed() + "/" + result.getTotal(),
            result.getElapsedMs()
        );

        int messageType = result.isAccepted()
            ? JOptionPane.INFORMATION_MESSAGE
            : JOptionPane.WARNING_MESSAGE;
        String detail = result.getDetail();
        String message = result.toString();
        if (detail != null && !detail.isBlank()) {
            message += "\n" + detail;
        }
        JOptionPane.showMessageDialog(
            view,
            message,
            "Judge Result",
            messageType
        );
    }

    private void showFailure(String message) {
        view.getResultLabel().setText(message);
        JOptionPane.showMessageDialog(
            view,
            message,
            "Submission Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    private static final class SubmissionOutcome {
        private final Submission submission;
        private final JudgeResult result;

        private SubmissionOutcome(Submission submission, JudgeResult result) {
            this.submission = submission;
            this.result = result;
        }
    }
}
