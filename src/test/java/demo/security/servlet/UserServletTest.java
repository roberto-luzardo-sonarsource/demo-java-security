package demo.security.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.security.util.SessionHeader;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServletTest {

    @Before
    public void setUpDatabase() throws Exception {
        try (Connection connection = DriverManager.getConnection(
                System.getenv("JDBC_URL"),
                System.getenv("JDBC_USER"),
                System.getenv("JDBC_PASSWORD"));
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS users (userid VARCHAR(32), username VARCHAR(32))");
            statement.execute("DELETE FROM users");
            statement.execute("INSERT INTO users (userid, username) VALUES ('1', 'alice')");
        }
    }

    @Test
    public void doGet_returnsEscapedUserFromDatabase() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getParameter("username")).thenReturn("alice");

        StringWriter body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));

        new UserServlet().doGet(request, response);

        assertTrue(body.toString().contains("<h2>User 1</h2>"));
    }

    @Test
    public void doPost_readsJsonSessionHeader() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        SessionHeader header = new SessionHeader("alice", "session-1");
        String encoded = Base64.encodeBase64String(new ObjectMapper().writeValueAsBytes(header));
        when(request.getHeader("Session-Auth")).thenReturn(encoded);

        StringWriter body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));

        new UserServlet().doPost(request, response);

        ArgumentCaptor<String> contentTypeCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).setContentType(contentTypeCaptor.capture());
        assertTrue(body.toString().contains("<h2>User 1</h2>"));
    }
}
