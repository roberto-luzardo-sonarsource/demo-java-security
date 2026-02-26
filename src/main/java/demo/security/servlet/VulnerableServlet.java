/*
 * SonarSource-Demos :: Demo Java Security
 * Copyright (C) 2026 SonarSource-Demos  
 * Licensed under the GNU LGPL v3 License
 */
package demo.security.servlet;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Intentionally vulnerable servlet for SonarQube security testing
 * This class contains multiple security vulnerabilities and code quality issues
 */
@WebServlet("/vulnerable")
public class VulnerableServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    // Hardcoded credentials - Security Hotspot
    private static final String DB_PASSWORD = "admin123";
    private static final String API_KEY = "sk-1234567890abcdef";
    
    // Weak hash algorithm - Security Vulnerability
    private static final String HASH_ALGORITHM = "MD5";
    
    private static final Logger LOGGER = Logger.getLogger(VulnerableServlet.class.getName());
    
    // Unused variable - Code Smell
    private String unusedVariable = "This variable is never used";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("login".equals(action)) {
            handleLogin(request, response);
        } else if ("search".equals(action)) {
            handleSearch(request, response);
        } else if ("file".equals(action)) {
            handleFileAccess(request, response);
        } else if ("admin".equals(action)) {
            handleAdminFunction(request, response);
        } else {
            // XSS Vulnerability - Direct output without encoding
            String message = request.getParameter("message");
            PrintWriter out = response.getWriter();
            out.println("<h1>Welcome! Your message: " + message + "</h1>");
            // Resource not closed - Bug
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
    
    /**
     * SQL Injection vulnerability example
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            // Hardcoded database connection - Security Hotspot
            conn = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", DB_PASSWORD);
            stmt = conn.createStatement();
            
            // SQL Injection vulnerability
            String sql = "SELECT * FROM users WHERE username = '" + username + 
                        "' AND password = '" + password + "'";
            rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                // Insecure cookie - Security Hotspot
                Cookie sessionCookie = new Cookie("sessionId", generateSessionId());
                sessionCookie.setHttpOnly(false); // Should be true
                sessionCookie.setSecure(false);   // Should be true for HTTPS
                response.addCookie(sessionCookie);
                
                response.getWriter().println("Login successful!");
            } else {
                response.getWriter().println("Invalid credentials");
            }
            
        } catch (Exception e) {
            // Empty catch block - Code Smell
            // Should log the exception
        } finally {
            // Resource leak - resources not properly closed
            // Should close rs, stmt, conn in finally block
        }
    }
    
    /**
     * More SQL injection and code quality issues
     */
    private void handleSearch(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String searchTerm = request.getParameter("q");
        String category = request.getParameter("category");
        
        // Null pointer exception risk
        if (searchTerm.length() > 0) { // No null check
            
            Connection conn = null;
            PreparedStatement pstmt = null;
            
            try {
                conn = getConnection();
                
                // Partially vulnerable - still injectable through category parameter
                String sql = "SELECT * FROM products WHERE name LIKE ? AND category = '" + category + "'";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, "%" + searchTerm + "%");
                
                ResultSet rs = pstmt.executeQuery();
                List<String> results = new ArrayList<>();
                
                while (rs.next()) {
                    results.add(rs.getString("name"));
                }
                
                // XSS vulnerability in output
                PrintWriter out = response.getWriter();
                out.println("<h2>Search results for: " + searchTerm + "</h2>");
                for (String result : results) {
                    out.println("<p>" + result + "</p>");
                }
                
            } catch (Exception e) {
                // Information disclosure - showing stack trace
                e.printStackTrace(response.getWriter());
            }
            // Resources not closed properly
        }
    }
    
    /**
     * Path traversal vulnerability
     */
    private void handleFileAccess(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String fileName = request.getParameter("file");
        
        // Path traversal vulnerability
        File file = new File("/app/data/" + fileName);
        
        // Potential information disclosure
        if (file.exists()) {
            try {
                byte[] content = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                response.getOutputStream().write(content);
            } catch (IOException e) {
                // Swallow exception - Code Smell
            }
        } else {
            response.sendError(404, "File not found: " + fileName); // Path disclosure
        }
    }
    
    /**
     * Authentication bypass and weak encryption
     */
    private void handleAdminFunction(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String token = request.getParameter("token");
        
        // Weak authentication check
        if (isValidToken(token)) {
            String secret = request.getParameter("secret");
            String encrypted = weakEncrypt(secret);
            
            response.getWriter().println("Encrypted secret: " + encrypted);
        } else {
            // Timing attack vulnerability - different response times
            try {
                Thread.sleep(1000); // Artificial delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            response.sendError(403, "Access denied");
        }
    }
    
    /**
     * Weak random number generation and poor token validation
     */
    private boolean isValidToken(String token) {
        // Weak random number generation
        Random random = new Random(); // Should use SecureRandom
        int expectedToken = random.nextInt(1000000);
        
        try {
            int userToken = Integer.parseInt(token);
            return userToken == expectedToken; // Predictable token
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Weak cryptography
     */
    private String weakEncrypt(String data) {
        try {
            // Weak hash algorithm
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = md.digest(data.getBytes());
            
            // Poor encoding
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(Integer.toHexString(b & 0xff));
            }
            return sb.toString();
            
        } catch (Exception e) {
            // Return sensitive information in exception
            return "Error: " + e.getMessage() + " for data: " + data;
        }
    }
    
    /**
     * Session ID generation with weak randomness
     */
    private String generateSessionId() {
        // Weak random number generation
        Random random = new Random(System.currentTimeMillis()); // Predictable seed
        
        StringBuilder sessionId = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            sessionId.append(Integer.toHexString(random.nextInt(16)));
        }
        
        return sessionId.toString();
    }
    
    /**
     * Database connection with hardcoded credentials
     */
    private Connection getConnection() throws Exception {
        // Multiple issues: hardcoded credentials, no connection pooling
        String url = "jdbc:mysql://localhost:3306/testdb";
        String username = "root";
        String password = "password123"; // Hardcoded password
        
        return DriverManager.getConnection(url, username, password);
    }
    
    // Dead code - unreachable method
    private void deadCode() {
        System.out.println("This method is never called");
        int x = 1;
    }
    
    // Method with too many parameters - Code Smell
    public void methodWithTooManyParameters(String param1, String param2, String param3, 
            String param4, String param5, String param6, String param7, String param8) {
        // Method complexity too high
        if (param1 != null) {
            if (param2 != null) {
                if (param3 != null) {
                    if (param4 != null) {
                        if (param5 != null) {
                            if (param6 != null) {
                                if (param7 != null) {
                                    if (param8 != null) {
                                        System.out.println("All parameters provided");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}