import demo.security.util.DBUtils;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertThrows;

public class DBUtilsTest {

    @Test
    public void constructor_requiresEnvironmentVariables() {
        assertThrows(SQLException.class, DBUtils::new);
    }
}
