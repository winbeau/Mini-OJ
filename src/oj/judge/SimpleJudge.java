package oj.judge;

import oj.core.*;
import java.util.Scanner;

public class SimpleJudge {
    public static String solve(String input) {
        Scanner sc = new Scanner(input);
        int a = sc.nextInt();
        int b = sc.nextInt();
    
        return String.valueOf(a + b);
    }

    public static JudgeResult judge(Problem p) {
        TestCase[] cases = p.getCases();
        int passed = 0;
        String detail = "";

        for (int i = 0; i < cases.length; i++) {
            String actual = solve(cases[i].getInput()).trim();
            String expected = cases[i].getExpected().trim();
            
            if (actual.equals(expected)) {
                passed++;
                System.out.println("case#" + i + " Right");
            } else {
                if (detail.isEmpty()) {
                    detail = "case#" + i + " Wrong! Expected: " + expected + " Result: " + actual;
                }
                System.out.println("case#" + i + " Wrong");
            }
        }

        Status status = (passed == cases.length) ? Status.AC : Status.WA;
        return new JudgeResult(status, passed, cases.length, detail);
    }
}
