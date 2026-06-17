package oj.core;

public class JudgeResult {
    private final Status status;
    private final int passed;
    private final int total;
    private final String detail;
    private final long elapsedMs;

    public JudgeResult(Status status, int passed, int total, String detail, long elapsedMs) {
        this.status = status;
        this.passed = passed;
        this.total = total;
        this.detail = detail;
        this.elapsedMs = elapsedMs;
    }

    public Status getStatus() {
        return status;
    }

    public int getPassed() {
        return passed;
    }

    public int getTotal() {
        return total;
    }

    public String getDetail() {
        return detail;
    }

    public long getElapsedMs() {
        return elapsedMs;
    }

    public boolean isAccepted() {
        return status == Status.AC;
    }

    @Override
    public String toString() {
        String pre = status + " " + passed + "/" + total + " (" + elapsedMs + "ms)";
        if (status == Status.AC) {
            return pre;
        } else {
            return pre + " (" + detail + ") ";
        }
    }
}
