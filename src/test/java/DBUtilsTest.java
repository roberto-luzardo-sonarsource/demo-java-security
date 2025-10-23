import demo.security.util.DBUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.mockito.Mockito.*;

public class DBUtilsTest {

    @Test
    public void testFindUsersParameterized() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        DBUtils dbUtils = new DBUtils(conn);
        dbUtils.findUsers("alice");
        verify(conn).prepareStatement("SELECT userid FROM users WHERE username = ?");
        verify(ps).setString(1, "alice");
    }

    @Test
    public void testFindItemParameterized() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        DBUtils dbUtils = new DBUtils(conn);
        dbUtils.findItem("123");
        verify(conn).prepareStatement("SELECT item_id FROM items WHERE item_id = ?");
        verify(ps).setString(1, "123");
    }
}
