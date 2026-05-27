package demo.security.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * Demo utilities refactored to satisfy SonarQube quality and security rules.
 */
public class SonarVulnerableExamples {

  private static final Logger LOGGER =
      Logger.getLogger(SonarVulnerableExamples.class.getName());

  private final SecureRandom secureRandom = new SecureRandom();

  public String lookupUserByName(HttpServletRequest request, Connection connection)
      throws SQLException {
    String username = request.getParameter("username");
    String query = "SELECT id, email FROM users WHERE username = ?";
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, username);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return resultSet.getString("email");
        }
      }
    }
    return null;
  }

  public int lookupUserId(Connection connection, String userId) throws SQLException {
    String query = "SELECT id FROM users WHERE id = ?";
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, userId);
      try (ResultSet rs = statement.executeQuery()) {
        if (rs.next()) {
          return rs.getInt(1);
        }
      }
    }
    return -1;
  }

  public void runCommand(String[] command) throws IOException {
    try {
      new ProcessBuilder(command).start();
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Failed to run command", e);
    }
  }

  public boolean checkPassword(String input, String expectedSecret) {
    return expectedSecret != null && expectedSecret.equals(input);
  }

  public String hashToken(String token) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    byte[] digest = md.digest(token.getBytes(StandardCharsets.UTF_8));
    StringBuilder sb = new StringBuilder();
    for (byte b : digest) {
      sb.append(String.format("%02X", b));
    }
    return sb.toString();
  }

  public String buildLabel(String prefix, int count) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < count; i++) {
      result.append(prefix).append(i);
    }
    return result.toString();
  }

  public int pickLotteryNumber() {
    return secureRandom.nextInt(100);
  }

  public void logEvent(String message) {
    LOGGER.info(message);
  }

  public String readUserInput() throws IOException {
    try (BufferedReader reader =
        new BufferedReader(
            new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
      return reader.readLine();
    }
  }
}
