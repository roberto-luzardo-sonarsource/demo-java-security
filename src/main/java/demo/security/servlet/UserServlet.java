package demo.security.servlet;

import demo.security.util.DBUtils;
import demo.security.util.SessionHeader;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String user = request.getParameter("username");
        try {
            DBUtils db = new DBUtils();
            List<String> users = db.findUsers(user);
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            users.forEach((result) -> {
                        out.print("<h2>User "+result+ "</h2>");
            });
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private SessionHeader getSessionHeader(HttpServletRequest request) {
        String sessionAuth = request.getHeader("Session-Auth");
        if (sessionAuth == null || sessionAuth.isBlank()) {
            return null;
        }
        try {
            byte[] decoded = Base64.decodeBase64(sessionAuth);
            try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(decoded))) {
                // Restrict deserialization to the expected class only
                ObjectInputFilter filter = ObjectInputFilter.Config.createFilter("demo.security.util.SessionHeader;!*" );
                in.setObjectInputFilter(filter);
                Object obj = in.readObject();
                if (obj instanceof SessionHeader) {
                    return (SessionHeader) obj;
                }
            }
        } catch (Exception ignored) {
            // Invalid header or tampered data; treat as unauthenticated
        }
        return null;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionHeader sessionHeader = getSessionHeader(request);
        if (sessionHeader == null) return;
        String user = sessionHeader.getUsername();
        try {
            DBUtils db = new DBUtils();
            List<String> users = db.findUsers(user);
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            users.forEach((result) -> {
                out.print("<h2>User "+result+ "</h2>");
            });
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
