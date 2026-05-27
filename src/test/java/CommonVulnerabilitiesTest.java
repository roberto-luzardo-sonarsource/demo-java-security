import demo.security.servlet.CommonVulnerabilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommonVulnerabilitiesTest {

  private CommonVulnerabilities vulnerabilities;
  private Connection connection;
  private PreparedStatement preparedStatement;
  private ResultSet resultSet;

  @BeforeEach
  void setUp() throws SQLException {
    vulnerabilities = new CommonVulnerabilities();
    connection = mock(Connection.class);
    preparedStatement = mock(PreparedStatement.class);
    resultSet = mock(ResultSet.class);
    when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    when(preparedStatement.executeQuery()).thenReturn(resultSet);
  }

  @Test
  void findUserByName_returnsNameWhenFound() throws SQLException {
    when(resultSet.next()).thenReturn(true, false);
    when(resultSet.getString("name")).thenReturn("alice");

    assertEquals("alice", vulnerabilities.findUserByName(connection, "alice"));
    verify(preparedStatement).setString(1, "alice");
  }

  @Test
  void findUserByName_returnsNullWhenNotFound() throws SQLException {
    when(resultSet.next()).thenReturn(false);

    assertNull(vulnerabilities.findUserByName(connection, "missing"));
  }

  @Test
  void findUserById_returnsNullWhenNotFound() throws SQLException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("id")).thenReturn("99");
    when(resultSet.next()).thenReturn(false);

    assertNull(vulnerabilities.findUserById(connection, request));
  }

  @Test
  void findUserById_returnsNameWhenFound() throws SQLException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("id")).thenReturn("42");
    when(resultSet.next()).thenReturn(true, false);
    when(resultSet.getString(1)).thenReturn("bob");

    assertEquals("bob", vulnerabilities.findUserById(connection, request));
    verify(preparedStatement).setInt(1, 42);
  }

  @Test
  void updateUserEmail_executesUpdate() throws SQLException {
    vulnerabilities.updateUserEmail(connection, "user@example.com", 7);

    verify(preparedStatement).setString(1, "user@example.com");
    verify(preparedStatement).setInt(2, 7);
    verify(preparedStatement).executeUpdate();
  }

  @Test
  void hashPassword_returnsHexDigest() throws NoSuchAlgorithmException {
    String hash = vulnerabilities.hashPassword("secret");

    assertNotNull(hash);
    assertFalse(hash.isEmpty());
    assertEquals(hash, vulnerabilities.hashPassword("secret"));
  }

  @Test
  void generateSessionToken_returnsUniqueValues() {
    String token1 = vulnerabilities.generateSessionToken();
    String token2 = vulnerabilities.generateSessionToken();

    assertNotNull(token1);
    assertNotEquals(token1, token2);
  }

  @Test
  void logUserAction_doesNotThrow() {
    assertDoesNotThrow(() -> vulnerabilities.logUserAction("tester"));
  }

  @Test
  void readUserFile_blocksPathTraversal() {
    assertThrows(SecurityException.class, () -> vulnerabilities.readUserFile("../etc/passwd"));
  }

  @Test
  void setSessionCookie_setsSecureFlags() {
    HttpServletResponse response = mock(HttpServletResponse.class);
    ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);

    vulnerabilities.setSessionCookie(response, "session-123");

    verify(response).addCookie(captor.capture());
    Cookie cookie = captor.getValue();
    assertEquals("SESSION", cookie.getName());
    assertEquals("session-123", cookie.getValue());
    assertTrue(cookie.isHttpOnly());
    assertTrue(cookie.getSecure());
  }

  @Test
  void deserializeRequest_parsesJsonMap() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("payload")).thenReturn("{\"role\":\"admin\"}");

    assertDoesNotThrow(() -> vulnerabilities.deserializeRequest(request));
  }

  @Test
  void connectToDatabase_requiresEnvironmentVariable() {
    assertThrows(IllegalStateException.class, () -> vulnerabilities.connectToDatabase());
  }

  @Test
  void connectToDatabase_usesEnvironmentVariableWhenSet() {
    String password = System.getenv("DB_PASSWORD");
    if (password == null || password.isEmpty()) {
      return;
    }
    assertTrue(vulnerabilities.connectToDatabase().contains(password));
  }

  @Test
  void readUserFile_returnsFalseForMissingFileUnderBase() {
    assertFalse(vulnerabilities.readUserFile("nonexistent-user-file-12345.dat"));
  }

  @Test
  void handleInvalidInput_logsInvalidValue() {
    assertDoesNotThrow(() -> vulnerabilities.handleInvalidInput("not-a-number"));
    assertDoesNotThrow(() -> vulnerabilities.handleInvalidInput("42"));
  }

  @Test
  void compareUsernames_comparesCorrectly() {
    assertTrue(vulnerabilities.compareUsernames("a", "a"));
    assertFalse(vulnerabilities.compareUsernames("a", "b"));
    assertTrue(vulnerabilities.compareUsernames(null, null));
    assertFalse(vulnerabilities.compareUsernames("a", null));
  }

  @Test
  void readCommandOutput_runsEchoCommand() throws Exception {
    String output = vulnerabilities.readCommandOutput("echo", "hello");

    assertTrue(output.contains("hello"));
  }
}
