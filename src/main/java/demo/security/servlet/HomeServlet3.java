/*
 * Copyright (C) 2025 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
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
public class HomeServlet3 extends HttpServlet {

  public HomeServlet3() {
    super();
  }

  @Override
  protected void doGet(HttpServletRequest request,
    HttpServletResponse response) throws ServletException, IOException {
    String name = request.getParameter("name");
    if (name != null) {
      name = name.trim();
      name = StringEscapeUtils.escapeHtml4(name);
    }
    else {
      name = "Guest";
    }

    response.setContentType("text/html; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");

    PrintWriter out = response.getWriter();
    try {
      out.print("<h2>Hello " + name + "</h2>");
    }
    finally {
      out.close();
    }
  }

  @Override
  protected void doPost(HttpServletRequest request,
    HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }
}
