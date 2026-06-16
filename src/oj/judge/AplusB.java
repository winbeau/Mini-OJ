package oj.judge;

import java.util.Scanner;

public class AplusB implements Solution {
    @Override
    public String solve(String input) {
        Scanner sc = new Scanner(input);
        return String.valueOf(sc.nextInt() + sc.nextInt());
    }
}
