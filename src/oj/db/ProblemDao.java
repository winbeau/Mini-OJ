package oj.db;

import oj.core.ProblemMeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProblemDao {
    public ProblemMeta meta(int id) throws SQLException {
        Connection connection = Db.get();
        synchronized (connection) {
            String sql =
                "SELECT title, judge_class, time_limit_ms FROM problems WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) {
                        throw new SQLException("Problem not found: " + id);
                    }
                    return new ProblemMeta(
                        result.getString("title"),
                        result.getString("judge_class"),
                        result.getLong("time_limit_ms")
                    );
                }
            }
        }
    }

    public Map<Integer, ProblemMeta> listMeta() throws SQLException {
        Connection connection = Db.get();
        synchronized (connection) {
            String sql =
                "SELECT id, title, judge_class, time_limit_ms FROM problems ORDER BY id";
            Map<Integer, ProblemMeta> problems = new LinkedHashMap<>();
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    problems.put(
                        result.getInt("id"),
                        new ProblemMeta(
                            result.getString("title"),
                            result.getString("judge_class"),
                            result.getLong("time_limit_ms")
                        )
                    );
                }
            }
            return problems;
        }
    }

    public void upsert(int id, ProblemMeta meta, int memoryLimitMb) throws SQLException {
        Connection connection = Db.get();
        synchronized (connection) {
            String sql =
                "INSERT INTO problems " +
                "(id, title, judge_class, time_limit_ms, mem_limit_mb) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "title=VALUES(title), judge_class=VALUES(judge_class), " +
                "time_limit_ms=VALUES(time_limit_ms), mem_limit_mb=VALUES(mem_limit_mb)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);
                statement.setString(2, meta.getTitle());
                statement.setString(3, meta.getJudgeClass());
                statement.setLong(4, meta.getTimeLimitMs());
                statement.setInt(5, memoryLimitMb);
                statement.executeUpdate();
            }
        }
    }
}
