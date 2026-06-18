package oj.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Db {
    private static final String URL =
        "jdbc:mysql://localhost:3306/minioj?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "root";

    private static Connection connection;

    private Db() {
    }

    public static synchronized Connection get() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASS);
        }
        return connection;
    }

    public static synchronized void close() {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException e) {
            System.err.println("[DB] Failed to close connection: " + e.getMessage());
        } finally {
            connection = null;
        }
    }
}
