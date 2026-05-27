import demo.security.servlet.Insecure;
import org.junit.Test;
import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InsecureTest {

    @Test
    public void taintedSQL_usesParameterizedQuery() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(request.getParameter("user")).thenReturn("alice");
        when(connection.prepareStatement("SELECT userid FROM users WHERE username = ?")).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString(1)).thenReturn("42");

        Insecure insecure = new Insecure();
        assertEquals("42", insecure.taintedSQL(request, connection));

        verify(statement).setString(1, "alice");
    }

    @Test
    public void hotspotSQL_returnsNullWhenNoRowFound() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(connection.prepareStatement("select userid from users WHERE username = ?")).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Insecure insecure = new Insecure();
        assertNull(insecure.hotspotSQL(connection, "bob"));

        verify(statement).setString(1, "bob");
    }
}
