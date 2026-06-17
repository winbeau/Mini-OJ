package oj.core;

public class Problem {
    private final int id;
    private final ProblemMeta meta;
    private final TestCase[] cases;

    public Problem(int id,  ProblemMeta meta, TestCase... cases) {
        this.id = id;
        this.meta = meta;
        this.cases = cases;
    }

    public int getId() {
        return id;
    }

    public ProblemMeta getProblemMeta() {
        return meta;
    }

    public String getTitle() {
        return meta.getTitle();
    }

    public String getJudgeClass() {
        return meta.getJudgeClass();
    }

    public long getTimeLimitMs() {
        return meta.getTimeLimitMs();
    }

    public TestCase[] getCases() {
        return cases;
    }
}
