package oj.core;

public class TestCase {
    private final String input;
    private final String expected;

    public TestCase(String input, String expected) {
        this.input = input;
        this.expected = expected;
    }

    public String getInput() {
        return input;
    }

    public String getExpected() {
        return expected;
    }
}
