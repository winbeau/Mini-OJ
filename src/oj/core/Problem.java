package oj.core;

public class Problem {
    private final int id;
    private final String title;
    private final TestCase[] cases;

    public Problem(int id, String title, TestCase... cases) {
        this.id = id;
        this.title = title;
        this.cases = cases;
    }

    public int geiId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public TestCase[] getCases() {
        return cases;
    }
}
