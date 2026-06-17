package oj;

import oj.core.*;
import oj.judge.*;
import oj.io.*;
import java.io.File;;

public class Main {
    public static void main(String[] args) {
        try {
            File problemDir = new File("problems/1");
            if (!problemDir.exists()) {
                System.out.println("No Problem Exists");
                return;
            }

            Problem problem = SingleFileProblemLoader.load(1, problemDir);
            System.out.println("==== [M4 Prepared] ====");
            System.out.println("Problem Name: " + problem.getTitle());
            System.out.println("Time Limit: " + problem.getTimeLimitMs() + "ms");
            System.out.println("Relective Class Name: " + problem.getJudgeClass());

            Judge judge = JudgeFactory.create(problem.getJudgeClass());

            Solution solution = input -> {
                String[] tokens = input.trim().split("\\s+");
                int a = Integer.parseInt(tokens[0]);
                int b = Integer.parseInt(tokens[1]);
                return String.valueOf(a + b);
            };

            JudgeResult result = judge.judge(problem, solution);
            System.out.print("Test Result: " + result);
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
