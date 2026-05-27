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
            statement.execute("CREATE TABLE IF NOT EXISTS items (item_id VARCHAR(32))");
            statement.execute("DELETE FROM users");
            statement.execute("DELETE FROM items");
            statement.execute("INSERT INTO users (userid, username) VALUES ('99', 'mallory')");
            statement.execute("INSERT INTO items (item_id) VALUES ('item-42')");
        }
    }

    @Test
    public void findUsers_returnsMatchingUserIds() throws Exception {
        DBUtils db = new DBUtils();
        List<String> users = db.findUsers("mallory");
        assertFalse(users.isEmpty());
        assertEquals("99", users.get(0));
    }

    @Test
    public void findItem_returnsMatchingItemIds() throws Exception {
        DBUtils db = new DBUtils();
        List<String> items = db.findItem("item-42");
        assertFalse(items.isEmpty());
        assertEquals("item-42", items.get(0));
    }
}
