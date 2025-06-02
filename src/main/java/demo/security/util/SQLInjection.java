package demo.security.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SQLInjection {
    public static void main(String[] args) {
        String userInput = "admin' OR '1'='1"; // Example of malicious input

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "user", "password");
            Statement stmt = conn.createStatement();

            // Vulnerable SQL statement
            String sql = "SELECT * FROM users WHERE username = '" + userInput + "'";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                System.out.println("User: " + rs.getString("username"));
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

