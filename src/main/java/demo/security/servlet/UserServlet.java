package demo.security.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.security.util.DBUtils;
import demo.security.util.SessionHeader;
import demo.security.util.WebUtils;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/users")
public class UserServlet extends HttpServlet {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String user = request.getParameter("username");
        writeUsers(response, user);
    }

    private SessionHeader getSessionHeader(HttpServletRequest request) {
        String sessionAuth = request.getHeader("Session-Auth");
        if (sessionAuth == null || sessionAuth.isEmpty()) {
            return null;
        }
        try {
            byte[] decoded = Base64.decodeBase64(sessionAuth);
            return OBJECT_MAPPER.readValue(decoded, SessionHeader.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionHeader sessionHeader = getSessionHeader(request);
        if (sessionHeader == null) {
            return;
        }
        writeUsers(response, sessionHeader.getUsername());
    }

    private void writeUsers(HttpServletResponse response, String user) throws IOException {
        try {
            DBUtils db = new DBUtils();
            List<String> users = db.findUsers(user);
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            for (String result : users) {
                out.print("<h2>User " + WebUtils.escapeHtml(result) + "</h2>");
            }
            out.close();
        } catch (SQLException e) {
            throw new ServletException("Failed to load users", e);
        }
    }
}
