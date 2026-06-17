package oj.io;

import oj.core.TestCase;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProblemLoader {
    private final String problemsRoot;

    public ProblemLoader(String problemsRoot) {
        this.problemsRoot = problemsRoot;
    }

    public List<TestCase> cases(int id) {
        File dir = new File(problemsRoot, String.valueOf(id));
        List<TestCase> result = new ArrayList<>();
        if (!dir.isDirectory()) return result;

        File[] inFiles = dir.listFiles((d, name) -> name.endsWith(".in"));
        if (inFiles == null || inFiles.length == 0) return result;

        Arrays.sort(inFiles, (a, b) -> {
            int numA = Integer.parseInt(a.getName().replaceAll("[^0-9]", ""));
            int numB = Integer.parseInt(b.getName().replaceAll("[^0-9]", ""));
            return Integer.compare(numA, numB);
        });

        for (File inFile : inFiles) {
            String baseName = inFile.getName().replace(".in", "");
            File outFile = new File(dir, baseName + ".out");
            if (!outFile.exists()) continue;

            try {
                String input = new String(Files.readAllBytes(inFile.toPath())).trim();
                String expected = new String(Files.readAllBytes(outFile.toPath())).trim();
                result.add(new TestCase(input, expected));
            } catch (IOException e) {
                System.err.println("Failed to read testcase: " + inFile.getName());
            }
        }
        return result;
    }
}
