/*
 * SonarSource-Demos :: Demo Java Security
 * Copyright (C) 2026 SonarSource-Demos  
 * Licensed under the GNU LGPL v3 License
 */
package demo.security.servlet;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.owasp.encoder.Encode;

/**
 * Intentionally vulnerable servlet for SonarQube security testing
 * This class contains multiple security vulnerabilities and code quality issues
 */
@WebServlet("/vulnerable")
public class VulnerableServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    // DB password is loaded from the DB_PASSWORD environment variable at use site
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
            String message = request.getParameter("message");
            PrintWriter out = response.getWriter();
            out.println("<h1>Welcome! Your message: " + Encode.forHtml(message) + "</h1>");
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

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", requireEnv("DB_PASSWORD"));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Cookie sessionCookie = new Cookie("sessionId", generateSessionId());
                    sessionCookie.setHttpOnly(false);
                    sessionCookie.setSecure(false);
                    response.addCookie(sessionCookie);

                    response.getWriter().println("Login successful!");
                } else {
                    response.getWriter().println("Invalid credentials");
                }
            }
        } catch (Exception e) {
            // Empty catch block - Code Smell (pre-existing)
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
                
                String sql = "SELECT * FROM products WHERE name LIKE ? AND category = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, "%" + searchTerm + "%");
                pstmt.setString(2, category);

                ResultSet rs = pstmt.executeQuery();
                List<String> results = new ArrayList<>();

                while (rs.next()) {
                    results.add(rs.getString("name"));
                }

                PrintWriter out = response.getWriter();
                out.println("<h2>Search results for: " + Encode.forHtml(searchTerm) + "</h2>");
                for (String result : results) {
                    out.println("<p>" + Encode.forHtml(result) + "</p>");
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

        Path baseDir = Paths.get("/app/data").toAbsolutePath().normalize();
        Path resolved = baseDir.resolve(fileName).normalize();
        if (!resolved.startsWith(baseDir)) {
            response.sendError(400, "Invalid file path");
            return;
        }

        File file = resolved.toFile();
        if (file.exists()) {
            try {
                byte[] content = Files.readAllBytes(resolved);
                response.getOutputStream().write(content);
            } catch (IOException e) {
                // Swallow exception - Code Smell
            }
        } else {
            response.sendError(404);
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
        String url = "jdbc:mysql://localhost:3306/testdb";
        String username = "root";
        String password = requireEnv("MYSQL_PASSWORD");

        return DriverManager.getConnection(url, username, password);
    }

    private static String requireEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException("Required environment variable " + name + " is not set");
        }
        return value;
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