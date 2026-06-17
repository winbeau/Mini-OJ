package oj.io;

import oj.core.*;
import java.io.*;
import java.nio.file.Files;

public class SingleFileProblemLoader {
    public static Problem load(int id, File dir) throws IOException {
        ProblemMeta meta = ConfigFile.read(dir);
        
        String input = readAll(new File(dir, "input.txt"));
        String expected = readAll(new File(dir, "output.txt"));
        
        return new Problem(id, meta, new TestCase[]{ new TestCase(input, expected) });
    }

    private static String readAll(File f) throws IOException {
        return new String(Files.readAllBytes(f.toPath())).trim();
    }
}
