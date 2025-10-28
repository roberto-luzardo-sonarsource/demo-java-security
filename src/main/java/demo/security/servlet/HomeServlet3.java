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
        // Default constructor
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        if (name != null) {
            name = name.trim();
            // Sanitize input to prevent XSS attacks using Apache Commons Text
            name = StringEscapeUtils.escapeHtml4(name);
        } else {
            name = "Guest";
        }
        
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        try {
            out.print("<h2>Hello " + name + "</h2>");
        } finally {
            out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        // Delegate POST requests to GET handler
        doGet(request, response);
    }
}
