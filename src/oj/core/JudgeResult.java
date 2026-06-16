package oj.core;

public class JudgeResult {
    private final Status status;
    private final int passed;
    private final int total;
    private final String detail;

    public JudgeResult(Status status, int passed, int total, String detail) {
        this.status = status;
        this.passed = passed;
        this.total = total;
        this.detail = detail;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        if (status == Status.AC) {
            return status + " " + passed + "/" + total;
        } else {
            return status + " " + passed + "/" + total + " (" + detail + ") ";
        }
    }
}
