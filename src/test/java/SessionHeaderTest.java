import demo.security.util.SessionHeader;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SessionHeaderTest {

    @Test
    public void storesUsernameAndSessionId() {
        SessionHeader header = new SessionHeader("alice", "session-123");
        assertEquals("alice", header.getUsername());
        assertEquals("session-123", header.getSessionId());

        header.setUsername("bob");
        header.setSessionId("session-456");
        assertEquals("bob", header.getUsername());
        assertEquals("session-456", header.getSessionId());
    }
}
