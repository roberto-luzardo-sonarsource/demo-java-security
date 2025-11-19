/*
 * Copyright (C) 2025 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package demo.security.servlet;

import java.io.IOException;
import org.apache.commons.text.StringEscapeUtils;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/helloWorld")
public class HomeServlet2 extends HttpServlet {

  public HomeServlet2() {
    super();
  }

  @Override
  protected void doGet(HttpServletRequest request,
    HttpServletResponse response) throws ServletException, IOException {
    String name = StringEscapeUtils.escapeHtml4(request.getParameter("name").trim());
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    out.print("<h2>Hello "+name+ "</h2>");
    out.close();
  }

  @Override
  protected void doPost(HttpServletRequest request,
    HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }

}
