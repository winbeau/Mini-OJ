package oj.core;

import java.io.Serializable;
import java.util.Arrays;

public class JudgeTask implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int submissionId;
    private final int problemId;
    private final String userName;
    private final String lang;
    private final byte[] source;
    private final long timeLimitMs;
    private final int memoryLimitMb;

    private JudgeResult result;
    private boolean done;

    public JudgeTask(
            int submissionId,
            int problemId,
            String userName,
            String lang,
            byte[] source) {
        this(submissionId, problemId, userName, lang, source, 2000L, 256);
    }

    public JudgeTask(
            int submissionId,
            int problemId,
            String userName,
            String lang,
            byte[] source,
            long timeLimitMs,
            int memoryLimitMb) {
        if (source == null) {
            throw new IllegalArgumentException("Source cannot be null");
        }
        if (timeLimitMs <= 0) {
            throw new IllegalArgumentException("Time limit must be positive");
        }
        if (memoryLimitMb <= 0) {
            throw new IllegalArgumentException("Memory limit must be positive");
        }

        this.submissionId = submissionId;
        this.problemId = problemId;
        this.userName = userName;
        this.lang = lang;
        this.source = Arrays.copyOf(source, source.length);
        this.timeLimitMs = timeLimitMs;
        this.memoryLimitMb = memoryLimitMb;
    }

    public synchronized void complete(JudgeResult result) {
        if (done) {
            return;
        }
        this.result = result;
        this.done = true;
        notifyAll();
    }

    public synchronized JudgeResult await() throws InterruptedException {
        while (!done) {
            wait();
        }
        return result;
    }

    public synchronized boolean isDone() {
        return done;
    }

    public int getSubmissionId() {
        return submissionId;
    }

    public int getProblemId() {
        return problemId;
    }

    public String getUserName() {
        return userName;
    }

    public String getLang() {
        return lang;
    }

    public byte[] getSource() {
        return Arrays.copyOf(source, source.length);
    }

    public long getTimeLimitMs() {
        return timeLimitMs;
    }

    public int getMemoryLimitMb() {
        return memoryLimitMb;
    }

    public synchronized JudgeResult getResult() {
        return result;
    }
}
