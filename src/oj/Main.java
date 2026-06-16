package oj;

import oj.core.*;
import oj.judge.AplusB;
import oj.judge.Judge;
import oj.judge.SimpleJudge;
import oj.judge.Solution;
import oj.judge.StandardJudge;

public class Main {
    public static void main(String[] args) {
        System.out.println("Mini-OJ judge ready");

        check();
    }

    public static void check() {
        Problem p = new Problem(1, "A+B Problem",
            new TestCase("1 5", "6"),
            new TestCase("10 20", "30"),
            new TestCase("0 0", "0")
        );

        Judge judge = new StandardJudge();
        Judge lbd = new StandardJudge((a, b) -> a.trim().equals(b.trim()));

        Solution goods = new AplusB();
        System.out.println("Normal submission -> " + judge.judge(p, goods));

        System.out.println("Lambda Judge -> " + lbd.judge(p, goods));

        Solution bads = input -> {
            throw new ArithmeticException("div zero");
        };
        System.out.println("Collapse submission -> " + judge.judge(p, bads));
    }
}
