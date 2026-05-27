package demo.security.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {

    private final Connection connection;

    public DBUtils() throws SQLException {
        String url = System.getenv().getOrDefault("JDBC_URL", "jdbc:mysql://localhost:3306/app");
        String user = System.getenv("JDBC_USER");
        String password = System.getenv("JDBC_PASSWORD");
        if (user == null || user.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalStateException("JDBC_USER and JDBC_PASSWORD environment variables must be set");
        }
        connection = DriverManager.getConnection(url, user, password);
    }

    public List<String> findUsers(String user) throws SQLException {
        String query = "SELECT userid FROM users WHERE username = ?";
        List<String> users = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(resultSet.getString(1));
                }
            }
        }
        return users;
    }

    public List<String> findItem(String itemId) throws SQLException {
        String query = "SELECT item_id FROM items WHERE item_id = ?";
        List<String> items = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, itemId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(resultSet.getString(1));
                }
            }
        }
        return items;
    }
}
