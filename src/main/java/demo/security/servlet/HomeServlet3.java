package demo.security.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.text.StringEscapeUtils;

@WebServlet("/helloWorld")
public class HomeServlet3 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public HomeServlet3() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        if (name != null) {
            name = name.trim();
        } else {
            name = "Guest";
        }
        
        response.setContentType("text/html");
        try (PrintWriter out = response.getWriter()) {
            // Sanitize user input to prevent XSS using Apache Commons Text
            String sanitizedName = StringEscapeUtils.escapeHtml4(name);
            out.print("<h2>Hello " + sanitizedName + "</h2>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
