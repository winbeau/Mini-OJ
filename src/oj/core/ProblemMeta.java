package oj.core;

public final class ProblemMeta {
    private final String title;
    private final String judgeClass;
    private final long timeLimitMs;

    public ProblemMeta(String title, String judgeClass, long timeLimitMs) {
        this.title = title;
        this.judgeClass = judgeClass;
        this.timeLimitMs = timeLimitMs;
    }

    public String getTitle() {
        return title;
    }

    public String getJudgeClass() {
        return judgeClass;
    }

    public long getTimeLimitMs() {
        return timeLimitMs;
    }
}
