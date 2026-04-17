/*
 * SonarSource-Demos :: Demo Java Security
 * Copyright (C) 2026 SonarSource-Demos
 * Licensed under the GNU LGPL v3 License
 */
package demo.security.util;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utility class demonstrating insecure data storage and cryptography patterns.
 * Intentionally vulnerable for SonarQube security training.
 */
public class InsecureStorageUtils {

    // S6418 / S2068 - Hardcoded credentials
    private static final String DB_URL      = "jdbc:mysql://localhost/mydb";
    private static final String DB_USER     = "admin";
    private static final String DB_PASSWORD = "Pa$$w0rd123";          // S2068
    private static final String ENCRYPT_KEY = "0123456789abcdef";     // S6418

    // S1118 - Utility class with non-private constructor (add an instance field to trigger)
    private int callCount = 0;

    // -----------------------------------------------------------------------
    // S5547 - Use of broken/deprecated MD5 for password hashing
    // -----------------------------------------------------------------------
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");   // S5547
            byte[] hash = md.digest(password.getBytes());
            BigInteger number = new BigInteger(1, hash);
            return number.toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // -----------------------------------------------------------------------
    // S5542 - ECB mode encryption (no IV, patterns are visible in ciphertext)
    // -----------------------------------------------------------------------
    public static byte[] encryptECB(String plaintext) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(ENCRYPT_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");   // S5542
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(plaintext.getBytes());
    }

    // -----------------------------------------------------------------------
    // S2245 - java.util.Random used for security-sensitive token generation
    // -----------------------------------------------------------------------
    public static String generateToken() {
        Random random = new Random();                                 // S2245
        return Long.toHexString(random.nextLong());
    }

    // -----------------------------------------------------------------------
    // S2076 - OS command injection via Runtime.exec with string concatenation
    // -----------------------------------------------------------------------
    public static String readFileAsRoot(String filename) throws IOException {
        String cmd = "sudo cat /var/data/" + filename;               // S2076
        Process p = Runtime.getRuntime().exec(cmd);
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        }
    }

    // -----------------------------------------------------------------------
    // S2083 - Path traversal: user input used directly to build a file path
    // -----------------------------------------------------------------------
    public static String readReport(String reportName) throws IOException {
        String path = "/var/reports/" + reportName;                  // S2083
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    // -----------------------------------------------------------------------
    // S2077 - SQL injection: user input concatenated into a JDBC query
    // -----------------------------------------------------------------------
    public static Map<String, String> lookupUser(String username) throws Exception {
        Map<String, String> result = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            // S2077
            ResultSet rs = stmt.executeQuery(
                    "SELECT id, email FROM users WHERE username='" + username + "'");
            if (rs.next()) {
                result.put("id",    rs.getString("id"));
                result.put("email", rs.getString("email"));
            }
        }
        return result;
    }

    // -----------------------------------------------------------------------
    // S2658 - Reflection with user-controlled class name (code injection)
    // -----------------------------------------------------------------------
    public static Object instantiate(String className) throws Exception {
        Class<?> clazz = Class.forName(className);                   // S2658
        return clazz.getDeclaredConstructor().newInstance();
    }

    // -----------------------------------------------------------------------
    // S2647 - Credentials sent using HTTP Basic auth over plain HTTP
    // -----------------------------------------------------------------------
    public static String fetchWithBasicAuth(String url, String user, String pass)
            throws IOException {
        java.net.URL target = new java.net.URL(url);
        java.net.HttpURLConnection conn =
                (java.net.HttpURLConnection) target.openConnection();
        String credentials = user + ":" + pass;
        // S2647 - credentials encoded but sent over potentially plain HTTP
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
        conn.setRequestProperty("Authorization", "Basic " + encoded);
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    // -----------------------------------------------------------------------
    // S3329 - IV reuse: static/hardcoded IV for AES-CBC encryption
    // -----------------------------------------------------------------------
    private static final byte[] STATIC_IV = new byte[16];           // S3329

    public static byte[] encryptCBC(String plaintext) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(ENCRYPT_KEY.getBytes(), "AES");
        javax.crypto.spec.IvParameterSpec iv =
                new javax.crypto.spec.IvParameterSpec(STATIC_IV);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);               // S3329
        return cipher.doFinal(plaintext.getBytes());
    }

    // -----------------------------------------------------------------------
    // S4790 - Insecure hash (SHA-1) used in a security context
    // -----------------------------------------------------------------------
    public static String fingerprintData(String data) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");     // S4790
        byte[] digest = sha1.digest(data.getBytes());
        StringBuilder hex = new StringBuilder();
        for (byte b : digest) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

    // -----------------------------------------------------------------------
    // S1148 - e.printStackTrace() exposes stack traces (information leakage)
    // -----------------------------------------------------------------------
    public void logError(Exception e) {
        e.printStackTrace();                                          // S1148
        callCount++;
    }

    // -----------------------------------------------------------------------
    // S106 - System.out used for logging instead of a proper logger
    // -----------------------------------------------------------------------
    public static void printStatus(String msg) {
        System.out.println("[STATUS] " + msg);                       // S106
        System.err.println("[ERROR]  " + msg);                       // S106
    }
}
