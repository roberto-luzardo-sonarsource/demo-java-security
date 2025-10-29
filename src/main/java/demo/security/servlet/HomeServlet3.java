package demo.security.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/helloWorld")
public class HomeServlet3 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String ERROR_MESSAGE = "An error occurred processing your request";

    public HomeServlet3() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        try {
            String name = request.getParameter("name");
            if (name != null) {
                name = sanitizeInput(name.trim());
            } else {
                name = "Guest";
            }
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.print("<h2>Hello " + name + "</h2>");
            out.close();
        } catch (IOException e) {
            // Log the error internally without exposing sensitive information
            handleError(response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        try {
            doGet(request, response);
        } catch (ServletException | IOException e) {
            // Log the error internally without exposing sensitive information
            handleError(response);
        }
    }

    private void handleError(HttpServletResponse response) {
        try {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        } catch (IOException e) {
            // In case sendError fails, we can't do much more
            // This would typically be logged to server logs
        }
    }

    private String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        // Basic HTML sanitization to prevent XSS
        return input.replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;")
                   .replace("/", "&#x2F;");
    }

}
