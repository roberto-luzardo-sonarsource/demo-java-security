package demo.security.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/helloWorld")
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String HEADING_CLOSE_TAG = "</h2>";

    public HomeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }


    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        try {
            String name = request.getParameter("name").trim();
            String safeName = encodeHtml(name);
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.print("<h2>Hello " + safeName + HEADING_CLOSE_TAG);
            out.close();
        } catch (Exception e) {
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (IOException ioe) {
                // Unable to send error response
            }
        }
    }

    private static String encodeHtml(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("&", "&amp;")
                     .replace("<", "&lt;")
                     .replace(">", "&gt;")
                     .replace("\"", "&quot;")
                     .replace("'", "&#x27;");
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

}
