package demo.security.servlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Insecure {

  public void badFunction(HttpServletRequest request) throws IOException {
    String obj = request.getParameter("data");
    if (obj == null) {
      return; // nothing to process
    }
    ObjectMapper mapper = new ObjectMapper();
    // Explicitly map to String without enabling default typing (removed dangerous polymorphic typing)
    String val = mapper.readValue(obj, String.class);
    // Use a dedicated temp directory; avoid TOCTOU pattern of delete+mkdir
    File tempDir = new File(System.getProperty("java.io.tmpdir"), "app-temp");
    if (!tempDir.exists()) {
      tempDir.mkdirs();
    }
    Files.exists(Paths.get(tempDir.getAbsolutePath(), val));
  }

  public String taintedSQL(HttpServletRequest request, Connection connection) throws SQLException {
    String user = request.getParameter("user");
    if (user == null) {
      return null;
    }
    try (PreparedStatement ps = connection.prepareStatement("SELECT userid FROM users WHERE username = ?")) {
      ps.setString(1, user);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getString(1);
        }
      }
    }
    return null;
  }
  
  public String hotspotSQL(Connection connection, String user) throws SQLException {
    if (user == null) {
      return null;
    }
    try (PreparedStatement ps = connection.prepareStatement("SELECT userid FROM users WHERE username = ?")) {
      ps.setString(1, user);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getString(1);
        }
      }
    }
    return null;
  }

  // --------------------------------------------------------------------------
  // Custom sources, sanitizer and sinks example
  // See file s3649JavaSqlInjectionConfig.json in root directory 
  // --------------------------------------------------------------------------

  public String getInput(String name) {
    // Empty (fake) source
    // To be a real source this should normally return something from an input
    // that can be user manipulated e.g. an HTTP request, a cmd line parameter, a form input...
    return "Hello World and " + name;
  }

  public void storeData(String input) {
    // Empty (fake) sink
    // To be a real sink this should normally build an SQL query from the input parameter
  }

  public void verifyData(String input) {
    // Empty (fake) sanitizer (sic)
    // To be a real sanitizer this should normally examine the input and sanitize it
    // for any attempt of user manipulation (eg escaping characters, quoting strings etc...)
  }

  public void processParam(String input) {
    // Empty method just for testing
  }

  public void doSomething() {
    String myInput = getInput("Olivier"); // Get data from a source
    processParam(myInput);
    storeData(myInput);                   // store data w/o sanitizing --> Injection vulnerability 
  }

  public void doSomethingSanitized() {
    String myInput = getInput("Cameron"); // Get data from a source
    verifyData(myInput);                  // Sanitize data
    processParam(myInput);
    storeData(myInput);                   // store data after sanitizing --> No injection vulnerability 
  }
}
