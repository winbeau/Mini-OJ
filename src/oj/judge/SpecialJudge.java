package oj.judge;

public class SpecialJudge extends AbstractJudge {
    private static final double EPS = 1e-6;

    @Override
    protected boolean compare(String expected, String actutal) {
        if (actutal == null) return false;

        try {
            double expVal = Double.parseDouble(expected.trim());
            double actVal = Double.parseDouble(actutal.trim());
            return Math.abs(expVal - actVal) < EPS;
        } catch (NumberFormatException e) {
            return expected.trim().equals(actutal.trim());
        }
    }
}
