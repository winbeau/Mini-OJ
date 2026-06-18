package oj.db;

import oj.core.JudgeResult;
import oj.core.Status;
import oj.core.Submission;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubmissionDao {
    public void save(Submission submission) throws SQLException {
        Connection connection = Db.get();
        synchronized (connection) {
            boolean previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                String sql =
                    "INSERT INTO submissions " +
                    "(problem_id, username, lang, status, passed, total, time_ms, src_path) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    JudgeResult result = submission.getResult();
                    statement.setInt(1, submission.getProblemId());
                    statement.setString(2, submission.getUserName());
                    statement.setString(3, submission.getLang());
                    statement.setString(
                        4, result == null ? "PENDING" : result.getStatus().name());
                    statement.setInt(5, result == null ? 0 : result.getPassed());
                    statement.setInt(6, result == null ? 0 : result.getTotal());
                    statement.setLong(7, result == null ? 0L : result.getElapsedMs());
                    statement.setString(8, submission.getSrcPath());
                    statement.executeUpdate();
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
        }
    }

    public List<Submission> listByUser(String username, int problemId, int limit)
            throws SQLException {
        List<Submission> submissions = new ArrayList<>();
        if (limit <= 0) {
            return submissions;
        }

        Connection connection = Db.get();
        synchronized (connection) {
            String sql =
                "SELECT problem_id, username, lang, status, passed, total, " +
                "time_ms, src_path FROM submissions " +
                "WHERE username = ? AND problem_id = ? " +
                "ORDER BY submitted_at DESC, id DESC LIMIT ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setInt(2, problemId);
                statement.setInt(3, limit);
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        Submission submission = new Submission(
                            result.getInt("problem_id"),
                            result.getString("username"),
                            result.getString("lang"),
                            result.getString("src_path")
                        );

                        String rawStatus = result.getString("status");
                        if (!"PENDING".equals(rawStatus)) {
                            submission.setResult(new JudgeResult(
                                Status.valueOf(rawStatus),
                                result.getInt("passed"),
                                result.getInt("total"),
                                "",
                                result.getLong("time_ms")
                            ));
                        }
                        submissions.add(submission);
                    }
                }
            }
        }
        return submissions;
    }
}
