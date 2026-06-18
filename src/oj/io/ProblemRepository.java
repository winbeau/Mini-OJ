package oj.io;

import oj.core.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProblemRepository {
    private final String problemsRoot;
    private final ProblemLoader loader;
    private final Map<Integer, Problem> cache = new HashMap<>();

    public ProblemRepository(String problemsRoot) {
        this.problemsRoot = problemsRoot;
        this.loader = new ProblemLoader(problemsRoot);
    }

    public Problem get(int id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }
        Problem p = loadFromDisk(id);
        if (p != null) {
            cache.put(id, p);
        }
        return p;
    }

    private Problem loadFromDisk(int id) {
        File dir = new File(problemsRoot, String.valueOf(id));
        if (!dir.exists()) return null;
        try {
            ProblemMeta meta = ConfigFile.read(dir);
            List<TestCase> cases = loader.cases(id);
            return new Problem(id, meta, cases);
        } catch (Exception e) {
            return null;
        }
    }

    public int cacheSize() {
        return cache.size();
    }

    public void evict(int id) {
        cache.remove(id);
    }
}
