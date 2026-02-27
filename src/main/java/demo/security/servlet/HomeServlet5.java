/*
 * SonarSource-Demos :: Demo Java Security
 * Copyright (C) 2026 SonarSource-Demos  
 * Licensed under the GNU LGPL v3 License
 */
package demo.security.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/helloWorld")
public class HomeServlet5 extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = Logger.getLogger(HomeServlet5.class.getName());

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException {
    String name = request.getParameter("name");
    
    // Validate input to prevent null pointer exception
    if (name == null || name.trim().isEmpty()) {
      name = "Anonymous";
    } 
    else {
      name = name.trim();
      // Escape HTML to prevent XSS attacks
      name = escapeHtml(name);
    }
    
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    
    try (PrintWriter out = response.getWriter()) {
      out.print("<h2>Hello " + name + "</h2>");
      out.print("<h2>Hello " + name + "</h2>");
      out.print("<h2>Hello " + name + "</h2>");
      out.print("<h2>Hello " + name + "</h2>");
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Error writing response", e);
      throw new ServletException("Unable to write response", e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException {
    response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    try (PrintWriter out = response.getWriter()) {
      out.write("POST method not supported for this endpoint");
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Error writing error response", e);
      throw new ServletException("Unable to write error response", e);
    }
  }
  
  /**
   * Simple HTML escaping to prevent XSS attacks
   * @param input the input string to escape
   * @return the escaped string
   */
  private static String escapeHtml(String input) {
    if (input == null) {
      return "";
    }
    return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
  }
}
