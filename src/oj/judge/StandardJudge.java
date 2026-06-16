package oj.judge;

public class StandardJudge extends AbstractJudge {
    private final OutputComparator cmptor;

    public StandardJudge() {
        this((a, b) -> a.trim().equals(b.trim()));
    }

    public StandardJudge(OutputComparator cmptor) {
        this.cmptor = cmptor;
    }

    @Override
    protected boolean compare(String expected, String actual) {
        if (actual == null) {
            return false;
        }
        return cmptor.same(expected, actual);
    }
}
