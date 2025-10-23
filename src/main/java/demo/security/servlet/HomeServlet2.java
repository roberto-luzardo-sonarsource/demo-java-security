package demo.security.servlet;

import demo.security.util.WebUtils;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/helloWorld")
public class HomeServlet2 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    @SuppressWarnings("java:S1989") // IOException already declared in method signature
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        String rawName = request.getParameter("name");
        // Sanitize using OWASP encoder to prevent XSS
        String safeName = WebUtils.sanitizeHtml(rawName != null ? rawName.trim() : "");

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print("<h2>Hello " + safeName + "</h2>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
