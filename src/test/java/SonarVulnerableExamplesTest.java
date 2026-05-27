import demo.security.util.SonarVulnerableExamples;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SonarVulnerableExamplesTest {

  @Test
  void lookupUserByName_returnsEmailWhenFound() throws SQLException {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);
    ResultSet resultSet = mock(ResultSet.class);
    HttpServletRequest request = mock(HttpServletRequest.class);

    when(request.getParameter("username")).thenReturn("alice");
    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(true, false);
    when(resultSet.getString("email")).thenReturn("alice@example.com");

    SonarVulnerableExamples examples = new SonarVulnerableExamples();
    assertEquals("alice@example.com", examples.lookupUserByName(request, connection));
    verify(statement).setString(1, "alice");
  }

  @Test
  void lookupUserByName_returnsNullWhenNotFound() throws SQLException {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);
    ResultSet resultSet = mock(ResultSet.class);
    HttpServletRequest request = mock(HttpServletRequest.class);

    when(request.getParameter("username")).thenReturn("missing");
    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(false);

    SonarVulnerableExamples examples = new SonarVulnerableExamples();
    assertNull(examples.lookupUserByName(request, connection));
  }

  @Test
  void lookupUserId_returnsIdWhenFound() throws SQLException {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);
    ResultSet resultSet = mock(ResultSet.class);

    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(true, false);
    when(resultSet.getInt(1)).thenReturn(42);

    SonarVulnerableExamples examples = new SonarVulnerableExamples();
    assertEquals(42, examples.lookupUserId(connection, "7"));
    verify(statement).setString(1, "7");
  }

  @Test
  void lookupUserId_returnsNegativeOneWhenNotFound() throws SQLException {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);
    ResultSet resultSet = mock(ResultSet.class);

    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(false);

    SonarVulnerableExamples examples = new SonarVulnerableExamples();
    assertEquals(-1, examples.lookupUserId(connection, "99"));
  }

  @Test
  void checkPassword_comparesWithEquals() {
    SonarVulnerableExamples examples = new SonarVulnerableExamples();
    assertTrue(examples.checkPassword("secret", "secret"));
    assertFalse(examples.checkPassword("wrong", "secret"));
    assertFalse(examples.checkPassword("secret", null));
  }

  @Test
  void hashToken_usesSha256() throws NoSuchAlgorithmException {
    SonarVulnerableExamples examples = new SonarVulnerableExamples();
    String hash = examples.hashToken("token");
    assertNotNull(hash);
    assertFalse(hash.isEmpty());
  }

  @Test
  void buildLabel_concatenatesPrefixAndIndex() {
    SonarVulnerableExamples examples = new SonarVulnerableExamples();
    assertEquals("item0item1item2", examples.buildLabel("item", 3));
    assertEquals("", examples.buildLabel("x", 0));
  }

  @Test
  void pickLotteryNumber_returnsValueInRange() {
    SonarVulnerableExamples examples = new SonarVulnerableExamples();
    int value = examples.pickLotteryNumber();
    assertTrue(value >= 0 && value < 100);
  }

  @Test
  void logEvent_doesNotThrow() {
    SonarVulnerableExamples examples = new SonarVulnerableExamples();
    assertDoesNotThrow(() -> examples.logEvent("test-event"));
  }
}
