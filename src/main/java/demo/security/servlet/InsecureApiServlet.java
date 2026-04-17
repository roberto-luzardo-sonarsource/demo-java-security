/*
 * SonarSource-Demos :: Demo Java Security
 * Copyright (C) 2026 SonarSource-Demos
 * Licensed under the GNU LGPL v3 License
 */
package demo.security.servlet;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Servlet demonstrating additional vulnerability categories for SonarQube training.
 * Intentionally insecure — do not use in production.
 */
@WebServlet("/api")
public class InsecureApiServlet extends HttpServlet {

    // S2092 - Cookie created without the Secure flag
    // S3330 - Cookie created without the HttpOnly flag
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String action = req.getParameter("action");
        String input  = req.getParameter("input");

        Cookie session = new Cookie("SESSIONID", req.getSession().getId()); // S2092, S3330
        resp.addCookie(session);

        if ("redirect".equals(action)) {
            openRedirect(resp, input);
        } else if ("fetch".equals(action)) {
            serverSideRequest(resp, input);
        } else if ("ldap".equals(action)) {
            ldapLookup(resp, input);
        } else if ("xml".equals(action)) {
            parseXml(resp, input);
        }
    }

    // S5146 - Open redirect: user-controlled URL passed directly to sendRedirect
    private void openRedirect(HttpServletResponse resp, String target) throws IOException {
        resp.sendRedirect(target);                                    // S5146
    }

    // S5144 - SSRF: user-controlled URL used to make a server-side HTTP request
    private void serverSideRequest(HttpServletResponse resp, String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection(); // S5144
        conn.setRequestMethod("GET");
        try (InputStream is = conn.getInputStream();
             OutputStream os = resp.getOutputStream()) {
            is.transferTo(os);
        }
    }

    // S4433 / S2255 - LDAP injection: user input unsafely embedded in LDAP filter
    private void ldapLookup(HttpServletResponse resp, String username) throws IOException {
        try {
            DirContext ctx = new InitialDirContext();
            // S4433 - user input concatenated directly into LDAP filter
            String filter = "(&(objectClass=user)(uid=" + username + "))"; // S4433
            ctx.search("ou=users,dc=example,dc=com", filter, null);
            resp.getWriter().write("Search done");
        } catch (Exception e) {
            resp.sendError(500);
        }
    }

    // S2755 - XXE: DocumentBuilderFactory with external entity processing enabled
    private void parseXml(HttpServletResponse resp, String xmlData) throws IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // S2755
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.parse(new ByteArrayInputStream(xmlData.getBytes()));
            resp.getWriter().write("Parsed");
        } catch (Exception e) {
            resp.sendError(500);
        }
    }

    // S5344 - Password stored in a String (should use char[])
    // S2384 - Mutable array returned directly from a method
    private static final byte[] SECRET_BYTES = {0x53, 0x33, 0x63, 0x72, 0x65, 0x74}; // S2384

    public static byte[] getSecret() {
        return SECRET_BYTES;                                          // S2384
    }

    // S1612 - Method reference can replace lambda (minor code smell for variety)
    // S1874 - Use of deprecated HttpUtils
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String body = req.getReader().lines()
                .reduce("", (a, b) -> a + b);                        // S1612

        // S5693 - No limit on request body size (potential DoS via large uploads)
        ObjectInputStream ois = new ObjectInputStream(req.getInputStream()); // S5135
        try {
            Object obj = ois.readObject();
            resp.getWriter().write(obj.toString());
        } catch (ClassNotFoundException e) {
            resp.sendError(400);
        }
    }
}
