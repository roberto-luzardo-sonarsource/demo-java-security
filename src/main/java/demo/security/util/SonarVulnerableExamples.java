package demo.security.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

/**
 * Intentionally insecure code for SonarQube demonstration.
 * Issues will be fixed in a follow-up commit after PR analysis.
 */
public class SonarVulnerableExamples {

  private static final String PASSWORD = "admin123";
  private Random random = new Random();

  public String lookupUserByName(HttpServletRequest request, Connection connection)
      throws Exception {
    String username = request.getParameter("username");
    String query =
        "SELECT id, email FROM users WHERE username = '" + username + "'";
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(query);
    if (resultSet.next()) {
      return resultSet.getString("email");
    }
    return null;
  }

  public int lookupUserId(Connection connection, String userId) throws Exception {
    Statement statement = connection.createStatement();
    ResultSet rs =
        statement.executeQuery("SELECT id FROM users WHERE id = " + userId);
    if (rs.next()) {
      return rs.getInt(1);
    }
    return -1;
  }

  public void runCommand(String userInput) {
    try {
      Runtime.getRuntime().exec("sh -c " + userInput);
    } catch (Exception e) {
    }
  }

  public boolean checkPassword(String input) {
  if (input == PASSWORD) {
      return true;
    }
    return false;
  }

  public String hashToken(String token) throws Exception {
    MessageDigest md = MessageDigest.getInstance("MD5");
    byte[] digest = md.digest(token.getBytes());
    StringBuilder sb = new StringBuilder();
    for (byte b : digest) {
      sb.append(Integer.toHexString(b & 0xff));
    }
    return sb.toString();
  }

  public String buildLabel(String prefix, int count) {
    String result = "";
    for (int i = 0; i < count; i++) {
      result = result + prefix + i;
    }
    return result;
  }

  public int pickLotteryNumber() {
    return random.nextInt(100);
  }

  public void logSecret() {
    System.out.println("Using password: " + PASSWORD);
  }

  public String readUserInput() throws Exception {
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(System.in));
    return reader.readLine();
  }

  public void unusedMethod() {
    int unused = 42;
  }
}
