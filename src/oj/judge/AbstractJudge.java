package oj.judge;

import oj.core.*;
import oj.exception.*;

public abstract class AbstractJudge implements Judge {

    @Override
    public final JudgeResult judge(Problem p, Solution s) {
        TestCase[] cases = p.getCases();
        int passed = 0;
        String detail = "";
        long start = System.currentTimeMillis();

        try {
            for (int i = 0; i < cases.length; i++) {
                String actual = s.solve(cases[i].getInput());

                if (compare(cases[i].getExpected(), actual)) {
                    passed++;
                } else if (detail.isEmpty()) {
                    detail = "case#" + i + " Expected:" + cases[i].getExpected().trim() + " Actual: " + (actual == null ? "null" : actual.trim());
                }
            }
        } catch (RuntimeException e) {
            long t = System.currentTimeMillis() - start;
            return new JudgeResult(Status.RE, passed, cases.length, "Runtime Error: " + e.getMessage(), t);
        }
        
        long elapsed = System.currentTimeMillis() - start;
        assert passed <= cases.length : "The number of passed cases do not exceed the total cases.";
        Status st = (passed == cases.length) ? Status.AC : Status.WA;
        return new JudgeResult(st, passed, cases.length, detail, elapsed);
    }

    protected abstract boolean compare(String expected, String actual);
}
