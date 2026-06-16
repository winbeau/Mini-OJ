public class Main {
    public static void main(String[] args) {
        System.out.println("Mini-OJ judge ready");

        check();
    }

    public static int solve(int a, int b) {
        return a + b;
    }

    public static void check() {
        int[] a = {1, 10, 0};
        int[] b = {5, 20, 0};

        int[] expected = {6, 30, 0};
        
        for (int i = 0; i < a.length; i++) {
            int res = solve(a[i], b[i]);
            if (res == expected[i]) {
                System.out.println("case#" + i + " Right");
            } else {
                System.out.println("case#" + i + " Wrong! Expected: " + expected[i] + "Result: " + res);
            }
        }
    }
}
