package demo.security.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {

    Connection connection;

    public DBUtils() throws SQLException {
        String jdbcUrl = System.getenv("JDBC_URL");
        String jdbcUser = System.getenv("JDBC_USER");
        String jdbcPassword = System.getenv("JDBC_PASSWORD");
        if (jdbcUrl == null || jdbcUser == null || jdbcPassword == null) {
            throw new SQLException(
                    "Database credentials must be provided via JDBC_URL, JDBC_USER, and JDBC_PASSWORD environment variables");
        }
        connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
    }

    public List<String> findUsers(String user) throws Exception {
        String query = "SELECT userid FROM users WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<String> users = new ArrayList<>();
                while (resultSet.next()) {
                    users.add(resultSet.getString(1));
                }
                return users;
            }
        }
    }

    public List<String> findItem(String itemId) throws Exception {
        String query = "SELECT item_id FROM items WHERE item_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, itemId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<String> items = new ArrayList<>();
                while (resultSet.next()) {
                    items.add(resultSet.getString(1));
                }
                return items;
            }
        }
    }
}
