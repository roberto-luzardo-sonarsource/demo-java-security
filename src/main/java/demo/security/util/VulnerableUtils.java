/*
 * SonarSource-Demos :: Demo Java Security
 * Copyright (C) 2026 SonarSource-Demos  
 * Licensed under the GNU LGPL v3 License
 */
package demo.security.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Utility class with various security flaws for testing SonarQube
 * Contains multiple categories of vulnerabilities and code smells
 */
public class VulnerableUtils {
    
    // Hardcoded secrets - Security Hotspots
    private static final String SECRET_KEY = "MySecretKey123!@#";
    private static final String AWS_ACCESS_KEY = "AKIAIOSFODNN7EXAMPLE";
    private static final String DATABASE_PASSWORD = "supersecret123";
    
    // Regex patterns with potential ReDoS vulnerabilities
    private static final Pattern VULNERABLE_REGEX = Pattern.compile("(a+)+");
    private static final Pattern EMAIL_REGEX = Pattern.compile("^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");
    
    // Class-level variables that are never used - Code Smells
    private static String unusedStaticField = "never used";
    private final Object unusedFinalField = new Object();
    
    /**
     * Command injection vulnerability
     */
    public static String executeCommand(String userInput) {
        try {
            // Command injection vulnerability
            String command = "ping -c 1 " + userInput;
            Process process = Runtime.getRuntime().exec(command);
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            
            // Resource leak - reader not closed properly
            return result.toString();
            
        } catch (IOException e) {
            // Information leakage through exception
            return "Error executing command: " + e.getMessage() + " for input: " + userInput;
        }
    }
    
    /**
     * XML External Entity (XXE) vulnerability setup
     */
    public static void processXML(String xmlContent) {
        try {
            // XXE vulnerability - DocumentBuilderFactory not configured securely
            javax.xml.parsers.DocumentBuilderFactory dbf = 
                javax.xml.parsers.DocumentBuilderFactory.newInstance();
            
            // Missing security configurations:
            // dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            // dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            
            javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
            InputStream xmlStream = new ByteArrayInputStream(xmlContent.getBytes());
            
            org.w3c.dom.Document doc = db.parse(xmlStream);
            System.out.println("XML processed: " + doc.getDocumentElement().getTagName());
            
        } catch (Exception e) {
            e.printStackTrace(); // Information disclosure through stack trace
        }
    }
    
    /**
     * Insecure deserialization vulnerability
     */
    public static Object deserializeObject(byte[] data) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bis);
            
            // Insecure deserialization - no validation of object types
            Object obj = ois.readObject();
            
            // Resources not closed in finally block
            return obj;
            
        } catch (Exception e) {
            // Swallow exception - Code Smell
            return null;
        }
    }
    
    /**
     * LDAP injection vulnerability
     */
    public static String searchLDAP(String username, String department) {
        try {
            // LDAP injection vulnerability
            String filter = "(&(uid=" + username + ")(department=" + department + "))";
            
            // Simulated LDAP search
            return "LDAP search with filter: " + filter;
            
        } catch (Exception e) {
            throw new RuntimeException("LDAP error for user: " + username, e);
        }
    }
    
    /**
     * Weak SSL/TLS configuration
     */
    public static String makeInsecureHTTPSRequest(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            
            // Disable SSL certificate validation - Security Vulnerability
            TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null; // Trust all certificates
                    }
                    
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        // No validation
                    }
                    
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        // No validation
                    }
                }
            };
            
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            connection.setSSLSocketFactory(sc.getSocketFactory());
            
            // Disable hostname verification
            connection.setHostnameVerifier((hostname, session) -> true);
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
            
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            
            return response.toString();
            
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Password storage with weak hashing
     */
    public static String hashPassword(String password) {
        try {
            // Weak password hashing - using MD5 without salt
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(password.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (Exception e) {
            // Return password in plain text on error - Security Vulnerability
            return password;
        }
    }
    
    /**
     * File path traversal vulnerability
     */
    public static String readUserFile(String fileName) {
        try {
            // Path traversal vulnerability - no input validation
            String basePath = "/app/user-files/";
            File file = new File(basePath + fileName);
            
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            
            // Resource leak - stream not properly closed
            return new String(data);
            
        } catch (Exception e) {
            // Information disclosure
            return "Error reading file: " + fileName + " - " + e.getMessage();
        }
    }
    
    /**
     * Race condition vulnerability
     */
    public static void processUserData(String userId, String data) {
        // Race condition - non-thread-safe operations on shared resource
        File userFile = new File("/tmp/user_" + userId + ".tmp");
        
        try {
            if (!userFile.exists()) {
                userFile.createNewFile();
                // Race condition window here
            }
            
            FileWriter writer = new FileWriter(userFile, true);
            writer.write(data + "\n");
            writer.close();
            
        } catch (Exception e) {
            // Empty catch block
        }
    }
    
    /**
     * Regular Expression Denial of Service (ReDoS)
     */
    public static boolean validateInput(String input) {
        // Catastrophic backtracking vulnerability
        return VULNERABLE_REGEX.matcher(input).matches();
    }
    
    /**
     * Weak random number generation for cryptographic purposes
     */
    public static String generateToken() {
        // Weak random number generation for security purposes
        java.util.Random random = new java.util.Random();
        StringBuilder token = new StringBuilder();
        
        for (int i = 0; i < 16; i++) {
            token.append(Integer.toHexString(random.nextInt(16)));
        }
        
        return token.toString();
    }
    
    /**
     * Information disclosure through debug output
     */
    public static void processPayment(String cardNumber, String cvv, String amount) {
        // Information disclosure - logging sensitive data
        System.out.println("Processing payment: Card=" + cardNumber + ", CVV=" + cvv + ", Amount=" + amount);
        
        // Simulate payment processing
        if (cardNumber.length() == 16) {
            System.out.println("Payment successful");
        } else {
            System.out.println("Invalid card number: " + cardNumber);
        }
    }
    
    /**
     * Resource leak with thread pool
     */
    public static void processTasksUnsafely(String[] tasks) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        for (String task : tasks) {
            executor.submit(() -> {
                try {
                    Thread.sleep(1000);
                    System.out.println("Processed: " + task);
                } catch (InterruptedException e) {
                    // Interrupt not handled properly
                }
            });
        }
        
        // Resource leak - executor not shutdown properly
        // Should call executor.shutdown() and executor.awaitTermination()
    }
    
    /**
     * Method with high cognitive complexity - Code Smell
     */
    public static String complexBusinessLogic(String input1, String input2, String input3, 
            boolean flag1, boolean flag2, boolean flag3) {
        
        String result = "";
        
        if (input1 != null) {
            if (input1.length() > 0) {
                if (flag1) {
                    if (input2 != null) {
                        if (input2.equals("admin")) {
                            if (flag2) {
                                if (input3 != null) {
                                    if (input3.contains("secret")) {
                                        if (flag3) {
                                            result = "access_granted";
                                        } else {
                                            result = "flag3_false";
                                        }
                                    } else {
                                        result = "no_secret";
                                    }
                                } else {
                                    result = "input3_null";
                                }
                            } else {
                                result = "flag2_false";
                            }
                        } else {
                            result = "not_admin";
                        }
                    } else {
                        result = "input2_null";
                    }
                } else {
                    result = "flag1_false";
                }
            } else {
                result = "empty_input1";
            }
        } else {
            result = "input1_null";
        }
        
        return result;
    }
    
    // Duplicate code - Code Smell
    public static String duplicateMethod1(String input) {
        if (input == null) return "";
        String processed = input.trim().toLowerCase();
        if (processed.length() > 10) {
            processed = processed.substring(0, 10);
        }
        return processed + "_suffix";
    }
    
    public static String duplicateMethod2(String input) {
        if (input == null) return "";
        String processed = input.trim().toLowerCase();
        if (processed.length() > 10) {
            processed = processed.substring(0, 10);
        }
        return processed + "_suffix";
    }
}