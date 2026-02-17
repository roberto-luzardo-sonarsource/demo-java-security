package demo.security.servlet;

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
  protected void doGet(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
    String name = request.getParameter("name");
    
    // Handle null or empty name parameter
    if (name == null || name.trim().isEmpty()) {
      name = "Guest";
    } else {
      name = name.trim();
      // Basic HTML encoding to prevent XSS
      name = htmlEncode(name);
    }
    
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    
    try (PrintWriter out = response.getWriter()) {
      out.print("<h2>Hello " + name + "</h2>");
    }
  }

  @Override
  protected void doPost(HttpServletRequest request,
                        HttpServletResponse response) throws ServletException, IOException
  {
    doGet(request, response);
  }

  /**
   * Simple HTML encoding to prevent XSS attacks
   * @param input the input string to encode
   * @return the HTML-encoded string
   */
  private static String htmlEncode(String input) {
    if (input == null) {
      return "";
    }
    return input.replace("&", "&amp;")
               .replace("<", "&lt;")
               .replace(">", "&gt;")
               .replace("\"", "&quot;")
               .replace("'", "&#x27;");
  }
}
