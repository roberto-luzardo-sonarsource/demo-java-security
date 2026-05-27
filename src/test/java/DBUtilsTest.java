import demo.security.util.DBUtils;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DBUtilsTest {

    @Before
    public void setUpDatabase() throws Exception {
        try (Connection connection = DriverManager.getConnection(
                System.getenv("JDBC_URL"),
                System.getenv("JDBC_USER"),
                System.getenv("JDBC_PASSWORD"));
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS users (userid VARCHAR(32), username VARCHAR(32))");
            statement.execute("DELETE FROM users");
            statement.execute("INSERT INTO users (userid, username) VALUES ('99', 'mallory')");
        }
    }

    @Test
    public void findUsers_returnsMatchingUserIds() throws Exception {
        DBUtils db = new DBUtils();
        List<String> users = db.findUsers("mallory");
        assertFalse(users.isEmpty());
        assertEquals("99", users.get(0));
    }
}
