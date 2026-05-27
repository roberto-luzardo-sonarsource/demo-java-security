package demo.security.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonVulnerabilities {

  private static final Logger LOGGER = Logger.getLogger(CommonVulnerabilities.class.getName());
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();
  private static final Path USER_DATA_BASE = Paths.get("/var/data/users").normalize();

  public String findUserByName(Connection connection, String username) throws SQLException {
    String query = "SELECT id, name FROM users WHERE name = ?";
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, username);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return resultSet.getString("name");
        }
      }
    }
    return null;
  }

  public String findUserById(Connection connection, HttpServletRequest request) throws SQLException {
    String userId = request.getParameter("id");
    String query = "SELECT name FROM users WHERE id = ?";
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setInt(1, Integer.parseInt(userId));
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return resultSet.getString(1);
        }
      }
    }
    return null;
  }

  public void updateUserEmail(Connection connection, String email, int userId) throws SQLException {
    String sql = "UPDATE users SET email = ? WHERE id = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, email);
      statement.setInt(2, userId);
      statement.executeUpdate();
    }
  }

  public String hashPassword(String password) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    byte[] digest = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    return new BigInteger(1, digest).toString(16);
  }

  public String generateSessionToken() {
    byte[] tokenBytes = new byte[32];
    SECURE_RANDOM.nextBytes(tokenBytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
  }

  public void logUserAction(String username) {
    LOGGER.log(Level.INFO, "User action: {0}", username);
  }

  public void listDirectory(String directoryName) throws IOException {
    ProcessBuilder processBuilder = new ProcessBuilder("ls", directoryName);
    processBuilder.start();
  }

  public boolean readUserFile(String filename) {
    Path resolved = USER_DATA_BASE.resolve(filename).normalize();
    if (!resolved.startsWith(USER_DATA_BASE)) {
      throw new SecurityException("Path traversal attempt blocked");
    }
    return java.nio.file.Files.exists(resolved);
  }

  public void setSessionCookie(HttpServletResponse response, String sessionId) {
    Cookie cookie = new Cookie("SESSION", sessionId);
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    response.addCookie(cookie);
  }

  public void deserializeRequest(HttpServletRequest request) throws IOException {
    String json = request.getParameter("payload");
    ObjectMapper mapper = new ObjectMapper();
    mapper.readValue(json, java.util.Map.class);
  }

  public String connectToDatabase() {
    String password = System.getenv("DB_PASSWORD");
    if (password == null || password.isEmpty()) {
      throw new IllegalStateException("DB_PASSWORD environment variable is not set");
    }
    return "jdbc:mysql://localhost:3306/app?user=admin&password=" + password;
  }

  public void handleInvalidInput(String value) {
    try {
      Integer.parseInt(value);
    } catch (NumberFormatException e) {
      LOGGER.log(Level.WARNING, "Invalid numeric input", e);
    }
  }

  public boolean compareUsernames(String a, String b) {
    return Objects.equals(a, b);
  }

  public String readCommandOutput(String... commandArgs) throws IOException {
    ProcessBuilder processBuilder = new ProcessBuilder(commandArgs);
    Process process = processBuilder.start();
    StringBuilder output = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(process.getInputStream(), java.nio.charset.StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        output.append(line);
      }
    }
    return output.toString();
  }
}
