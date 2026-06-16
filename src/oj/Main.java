package oj;

import oj.core.*;
import oj.judge.SimpleJudge;

public class Main {
    public static void main(String[] args) {
        System.out.println("Mini-OJ judge ready");

        check();
    }

    public static void check() {
        Problem problem = new Problem(1, "A+B Problem",
            new TestCase("1 5", "6"),
            new TestCase("10 20", "30"),
            new TestCase("0 0", "0")
        );

        JudgeResult result = SimpleJudge.judge(problem);
        System.out.println("Problem " + problem.geiId());
        System.out.println("Result: " + result);
    }
}
