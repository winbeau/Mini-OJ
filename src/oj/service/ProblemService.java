package oj.service;

import oj.core.Problem;
import oj.core.ProblemMeta;
import oj.core.TestCase;
import oj.db.ProblemDao;
import oj.io.ProblemLoader;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ProblemService {
    private final ProblemDao problemDao;
    private final ProblemLoader problemLoader;

    public ProblemService(ProblemDao problemDao, ProblemLoader problemLoader) {
        this.problemDao = problemDao;
        this.problemLoader = problemLoader;
    }

    public Problem load(int id) throws SQLException {
        ProblemMeta meta = problemDao.meta(id);
        List<TestCase> cases = problemLoader.cases(id);
        return new Problem(id, meta, cases);
    }

    public ProblemMeta meta(int id) throws SQLException {
        return problemDao.meta(id);
    }

    public Map<Integer, ProblemMeta> listMeta() throws SQLException {
        return problemDao.listMeta();
    }
}
