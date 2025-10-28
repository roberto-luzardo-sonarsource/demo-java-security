package demo.security.servlet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class HomeServlet3Test {

    private HomeServlet3 servlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private StringWriter stringWriter;
    private PrintWriter writer;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        servlet = new HomeServlet3();
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    public void testDoGet_WithValidName() throws ServletException, IOException {
        // Given
        when(request.getParameter("name")).thenReturn("John");

        // When
        servlet.doGet(request, response);

        // Then
        writer.flush();
        String result = stringWriter.toString();
        assertTrue(result.contains("Hello John"));
        verify(response).setContentType("text/html");
    }

    @Test
    public void testDoGet_WithNameContainingWhitespace() throws ServletException, IOException {
        // Given
        when(request.getParameter("name")).thenReturn("  Jane  ");

        // When
        servlet.doGet(request, response);

        // Then
        writer.flush();
        String result = stringWriter.toString();
        assertTrue(result.contains("Hello Jane"));
    }

    @Test
    public void testDoGet_WithNullName() throws ServletException, IOException {
        // Given
        when(request.getParameter("name")).thenReturn(null);

        // When
        servlet.doGet(request, response);

        // Then
        writer.flush();
        String result = stringWriter.toString();
        assertTrue(result.contains("Hello Guest"));
    }

    @Test
    public void testDoGet_WithXSSAttempt() throws ServletException, IOException {
        // Given
        String maliciousInput = "<script>alert('XSS')</script>";
        when(request.getParameter("name")).thenReturn(maliciousInput);

        // When
        servlet.doGet(request, response);

        // Then
        writer.flush();
        String result = stringWriter.toString();
        // Verify that the HTML is escaped
        assertTrue(result.contains("&lt;script&gt;"));
        assertTrue(result.contains("&lt;/script&gt;"));
        // Ensure the raw script tag is not present
        assertTrue(!result.contains("<script>"));
    }

    @Test
    public void testDoGet_WithSpecialCharacters() throws ServletException, IOException {
        // Given
        when(request.getParameter("name")).thenReturn("O'Brien & <Company>");

        // When
        servlet.doGet(request, response);

        // Then
        writer.flush();
        String result = stringWriter.toString();
        // Verify special characters are escaped
        assertTrue(result.contains("&amp;")); // & should be escaped
        assertTrue(result.contains("&lt;")); // < should be escaped
        assertTrue(result.contains("&gt;")); // > should be escaped
    }

    @Test
    public void testDoGet_WithIOException() throws ServletException, IOException {
        // Given
        when(request.getParameter("name")).thenReturn("Test");
        when(response.getWriter()).thenThrow(new IOException("Test exception"));

        // When
        servlet.doGet(request, response);

        // Then
        // Verify error handling - should attempt to send error
        verify(response, atLeastOnce()).sendError(anyInt(), anyString());
    }

    @Test
    public void testDoPost() throws ServletException, IOException {
        // Given
        when(request.getParameter("name")).thenReturn("PostTest");

        // When
        servlet.doPost(request, response);

        // Then
        writer.flush();
        String result = stringWriter.toString();
        assertTrue(result.contains("Hello PostTest"));
    }

    @Test
    public void testConstructor() {
        // Test that constructor works without errors
        HomeServlet3 newServlet = new HomeServlet3();
        assertNotNull(newServlet);
    }
}
