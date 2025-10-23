package demo.security.util;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {

    Connection connection;
    public DBUtils() throws SQLException {
        connection = DriverManager.getConnection(
                "mYJDBCUrl", "myJDBCUser", "myJDBCPass");
    }

    // Constructor for testing / dependency injection
    public DBUtils(Connection connection) {
        this.connection = connection;
    }

    public List<String> findUsers(String user) throws SQLException {
        String sql = "SELECT userid FROM users WHERE username = ?";
        List<String> users = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(rs.getString(1));
                }
            }
        }
        return users;
    }

    public List<String> findItem(String itemId) throws SQLException {
        String sql = "SELECT item_id FROM items WHERE item_id = ?";
        List<String> items = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(rs.getString(1));
                }
            }
        }
        return items;
    }
}
