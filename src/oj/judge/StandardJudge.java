package oj.judge;

public class StandardJudge extends AbstractJudge {
    @Override
    protected boolean compare(String expected, String actual) {
        if (actual == null) {
            return false;
        }
        return expected.trim().equals(actual.trim());
    }
}
