/*
 * Copyright (C) 2025 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
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
public class HomeServlet2 extends HttpServlet {
  private static final long serialVersionUID = 1L;

  public HomeServlet2() {
    super();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) 
      throws ServletException, IOException {
    String name = request.getParameter("name");
    if (name != null) {
      name = name.trim();
      // Sanitize user input to prevent XSS attacks
      name = StringEscapeUtils.escapeHtml4(name);
    } else {
      name = "Guest";
    }
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    out.print("<h2>Hello " + name + "</h2>");
    out.close();
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) 
      throws ServletException, IOException {
    doGet(request, response);
  }
}
