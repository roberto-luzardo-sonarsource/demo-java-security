package demo.security.servlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeServlet3Test {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private HomeServlet3 servlet;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() {
        servlet = new HomeServlet3();
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
    }

    private void setupMocks() throws IOException {
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void constructor_shouldCreateInstance() {
        HomeServlet3 testServlet = new HomeServlet3();
        assertNotNull(testServlet);
    }

    @Test
    void doGet_withValidName_shouldReturnGreeting() throws ServletException, IOException {
        setupMocks();
        when(request.getParameter("name")).thenReturn("John");

        servlet.doGet(request, response);

        verify(response).setContentType("text/html; charset=UTF-8");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).getWriter();
        
        printWriter.flush();
        String output = stringWriter.toString();
        assertEquals("<h2>Hello John</h2>", output);
    }

    @Test
    void doGet_withNullName_shouldReturnGuestGreeting() throws ServletException, IOException {
        setupMocks();
        when(request.getParameter("name")).thenReturn(null);

        servlet.doGet(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertEquals("<h2>Hello Guest</h2>", output);
    }

    @Test
    void doGet_withXSSAttempt_shouldEscapeHtmlCharacters() throws ServletException, IOException {
        setupMocks();
        when(request.getParameter("name")).thenReturn("<script>alert('xss')</script>");

        servlet.doGet(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("&lt;script&gt;"));
        assertTrue(output.contains("&lt;/script&gt;"));
        assertFalse(output.contains("<script>"));
    }

    @Test
    void doGet_withWhitespace_shouldTrimName() throws ServletException, IOException {
        setupMocks();
        when(request.getParameter("name")).thenReturn("  Alice  ");

        servlet.doGet(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertEquals("<h2>Hello Alice</h2>", output);
    }

    @Test
    void doPost_shouldDelegateToDoGet() throws ServletException, IOException {
        setupMocks();
        when(request.getParameter("name")).thenReturn("TestUser");

        servlet.doPost(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertEquals("<h2>Hello TestUser</h2>", output);
    }

    @Test
    void doGet_whenIOException_shouldPropagateException() throws IOException {
        when(request.getParameter("name")).thenReturn("Test");
        when(response.getWriter()).thenThrow(new IOException("Writer error"));

        assertThrows(IOException.class, () -> servlet.doGet(request, response));
    }
}
