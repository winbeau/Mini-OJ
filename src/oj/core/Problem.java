package oj.core;

import java.util.List;

public class Problem {
    private final int id;
    private final ProblemMeta meta;
    private final List<TestCase> cases;

    public Problem(int id, ProblemMeta meta, List<TestCase> cases) {
        this.id = id;
        this.meta = meta;
        this.cases = cases;
    }

    public int getId() {
        return id;
    }

    public ProblemMeta getMeta() {
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

    public List<TestCase> getCases() {
        return cases;
    }
}
