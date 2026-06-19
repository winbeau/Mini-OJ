package oj.gui;

import oj.core.JudgeResult;
import oj.core.JudgeTask;
import oj.core.ProblemMeta;
import oj.db.SubmissionDao;
import oj.judge.MachineJudge;
import oj.judge.queue.JudgeQueue;
import oj.judge.queue.JudgeWorker;
import oj.service.ProblemService;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class OjController {
    private static final int WORKER_COUNT = 4;
    private static final int QUEUE_CAPACITY = 64;
    private static final int MEMORY_LIMIT_MB = 256;
    private static final String USER_NAME = "student";

    private final OjFrame view;
    private final ProblemService problemService;
    private final MachineJudge machineJudge;
    private final SubmissionDao submissionDao;
    private final JudgeQueue<JudgeTask> queue;
    private final Map<Integer, ProblemMeta> problemMeta = new HashMap<>();
    private final AtomicInteger submissionIdGenerator =
        new AtomicInteger((int) (System.currentTimeMillis() % 1_000_000_000L));
    private final AtomicInteger activeSubmissions = new AtomicInteger();

    public OjController(
            OjFrame view,
            ProblemService problemService,
            MachineJudge machineJudge,
            SubmissionDao submissionDao) {
        this.view = view;
        this.problemService = problemService;
        this.machineJudge = machineJudge;
        this.submissionDao = submissionDao;
        this.queue = new JudgeQueue<>(QUEUE_CAPACITY);

        startWorkers();
        initProblems();
        bindEvents();
    }

    private void startWorkers() {
        for (int i = 0; i < WORKER_COUNT; i++) {
            Thread worker = new Thread(
                new JudgeWorker(queue, submissionDao, machineJudge),
                "judge-worker-" + i
            );
            worker.setDaemon(true);
            worker.start();
        }
    }

    private void initProblems() {
        try {
            Map<Integer, ProblemMeta> all = problemService.listMeta();
            problemMeta.clear();
            problemMeta.putAll(all);

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
                "Failed to load problems: " + safeMessage(e),
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
            showWarning("Please select a problem.");
            return;
        }

        String language = view.getSelectedLang();
        String sourceCode = view.getSourceCode();
        if (language == null) {
            showWarning("Please select a language.");
            return;
        }
        if (sourceCode.isBlank()) {
            showWarning("Source code cannot be empty.");
            return;
        }

        ProblemMeta meta = problemMeta.get(problemId);
        if (meta == null) {
            showWarning("Problem metadata is unavailable.");
            return;
        }

        int submissionId = submissionIdGenerator.getAndIncrement();
        JudgeTask task = new JudgeTask(
            submissionId,
            problemId,
            USER_NAME,
            language,
            sourceCode.getBytes(StandardCharsets.UTF_8),
            meta.getTimeLimitMs(),
            MEMORY_LIMIT_MB
        );

        int active = activeSubmissions.incrementAndGet();
        view.getResultLabel().setText(
            "Queued #" + submissionId + " (" + active + " active)"
        );

        SwingWorker<JudgeResult, Void> worker = new SwingWorker<JudgeResult, Void>() {
            @Override
            protected JudgeResult doInBackground() throws Exception {
                queue.put(task);
                return task.await();
            }

            @Override
            protected void done() {
                int remaining = activeSubmissions.decrementAndGet();
                try {
                    JudgeResult result = get();
                    showResult(task, result, remaining);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    showFailure("Submission was interrupted.", remaining);
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    showFailure(
                        "Submission failed: " + safeMessage(cause == null ? e : cause),
                        remaining
                    );
                }
            }
        };
        worker.execute();
    }

    private void showResult(JudgeTask task, JudgeResult result, int remaining) {
        view.getResultLabel().setText(
            remaining == 0
                ? result.toString()
                : result + " (" + remaining + " active)"
        );
        view.appendHistory(
            task.getSubmissionId(),
            task.getProblemId(),
            task.getLang(),
            result.getStatus().name(),
            result.getPassed() + "/" + result.getTotal(),
            result.getElapsedMs()
        );

        if (!result.isAccepted()) {
            JOptionPane.showMessageDialog(
                view,
                result.toString() + "\n" + result.getDetail(),
                result.getStatus().name(),
                JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void showFailure(String message, int remaining) {
        view.getResultLabel().setText(
            remaining == 0 ? message : message + " (" + remaining + " active)"
        );
        JOptionPane.showMessageDialog(
            view,
            message,
            "Submission Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(
            view,
            message,
            "Submission",
            JOptionPane.WARNING_MESSAGE
        );
    }

    private String safeMessage(Throwable error) {
        String message = error.getMessage();
        return message == null ? error.getClass().getSimpleName() : message;
    }
}
