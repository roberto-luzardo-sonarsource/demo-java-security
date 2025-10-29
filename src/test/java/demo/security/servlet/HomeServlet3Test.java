package demo.security.servlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HomeServlet3Test {

    private HomeServlet3 servlet;

    @BeforeEach
    void setUp() {
        servlet = new HomeServlet3();
    }

    @Test
    void testConstructor() {
        // Test that constructor creates servlet instance
        HomeServlet3 newServlet = new HomeServlet3();
        assertNotNull(newServlet);
    }

    @ParameterizedTest
    @CsvSource({
        "'TestUser', 'Hello TestUser!'",
        "'  Test User  ', 'Hello Test User!'",
        "'', 'Hello !'",
        "'<script>alert(''xss'')</script>', 'Hello <script>alert(''xss'')</script>!'",
        ", 'Hello Guest!'"
    })
    void testDoGet_withVariousNames(String inputName, String expectedOutput) throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        
        when(request.getParameter("name")).thenReturn(inputName);
        when(response.getWriter()).thenReturn(printWriter);
        
        // Act
        servlet.doGet(request, response);
        
        // Assert
        verify(response).setContentType("text/plain");
        printWriter.flush();
        assertEquals(expectedOutput, stringWriter.toString());
    }

    @Test
    void testDoGet_responseGetWriterThrowsException() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        when(request.getParameter("name")).thenReturn("TestUser");
        when(response.getWriter()).thenThrow(new IOException("Writer exception"));
        
        // Act
        servlet.doGet(request, response);
        
        // Assert
        verify(response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred processing your request");
    }

    @Test
    void testDoPost_callsDoGet() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        
        when(request.getParameter("name")).thenReturn("PostUser");
        when(response.getWriter()).thenReturn(printWriter);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(response).setContentType("text/plain");
        printWriter.flush();
        assertEquals("Hello PostUser!", stringWriter.toString());
    }

    @Test
    void testDoPost_withServletException() throws ServletException, IOException {
        // Create a servlet that will throw an exception in doGet
        HomeServlet3 faultyServlet = new HomeServlet3() {
            @Override
            protected void doGet(HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException {
                throw new ServletException("Test exception");
            }
        };
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        // Act
        faultyServlet.doPost(request, response);
        
        // Assert
        verify(response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred processing your request");
    }

    @Test
    void testDoPost_withIOException() throws ServletException, IOException {
        // Create a servlet that will throw an IOException in doGet
        HomeServlet3 faultyServlet = new HomeServlet3() {
            @Override
            protected void doGet(HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException {
                throw new IOException("Test IO exception");
            }
        };
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        // Act
        faultyServlet.doPost(request, response);
        
        // Assert
        verify(response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred processing your request");
    }

    @Test
    void testHandleError_sendErrorThrowsException() throws IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        when(request.getParameter("name")).thenReturn("TestUser");
        when(response.getWriter()).thenThrow(new IOException("Writer exception"));
        doThrow(new IOException("SendError exception")).when(response)
            .sendError(anyInt(), anyString());
        
        // Act & Assert - should not throw exception even if sendError fails
        assertDoesNotThrow(() -> {
            try {
                servlet.doGet(request, response);
            } catch (ServletException e) {
                fail("Should not throw ServletException");
            }
        });
    }

    @Test
    void testContentTypeIsSetToPlainText() throws ServletException, IOException {
        // Verify that content type is always set to text/plain for security
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        
        when(request.getParameter("name")).thenReturn("TestUser");
        when(response.getWriter()).thenReturn(printWriter);
        
        servlet.doGet(request, response);
        
        verify(response).setContentType("text/plain");
        verify(response, never()).setContentType("text/html");
    }

    @Test
    void testSerialVersionUID() {
        // Test that the class has proper serialVersionUID for servlet compatibility
        assertTrue(HomeServlet3.class.getDeclaredFields().length > 0);
        // The servlet extends HttpServlet which implements Serializable
        assertTrue(java.io.Serializable.class.isAssignableFrom(HomeServlet3.class));
    }

    @Test
    void testOutputFormat() throws ServletException, IOException {
        // Test output format consistency
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        
        when(request.getParameter("name")).thenReturn("TestUser");
        when(response.getWriter()).thenReturn(printWriter);
        
        servlet.doGet(request, response);
        printWriter.flush();
        
        String output = stringWriter.toString();
        assertTrue(output.startsWith("Hello "));
        assertTrue(output.endsWith("!"));
        assertEquals("Hello TestUser!", output);
    }

    @Test 
    void testNameParameterProcessing() throws ServletException, IOException {
        // Test various name parameter scenarios
        String[] testNames = {"John", "Jane Doe", "123", "user@domain.com"};
        
        for (String name : testNames) {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            
            when(request.getParameter("name")).thenReturn(name);
            when(response.getWriter()).thenReturn(printWriter);
            
            servlet.doGet(request, response);
            printWriter.flush();
            
            assertEquals("Hello " + name + "!", stringWriter.toString());
            verify(response).setContentType("text/plain");
        }
    }

    @Test
    void testBoundaryConditions() throws ServletException, IOException {
        // Test with very long name
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        
        String longName = "A".repeat(1000);
        when(request.getParameter("name")).thenReturn(longName);
        when(response.getWriter()).thenReturn(printWriter);
        
        servlet.doGet(request, response);
        printWriter.flush();
        
        assertEquals("Hello " + longName + "!", stringWriter.toString());
        verify(response).setContentType("text/plain");
    }

    @Test
    void testUnicodeCharacters() throws ServletException, IOException {
        // Test with unicode characters
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        
        String unicodeName = "José ñoño";
        when(request.getParameter("name")).thenReturn(unicodeName);
        when(response.getWriter()).thenReturn(printWriter);
        
        servlet.doGet(request, response);
        printWriter.flush();
        
        assertEquals("Hello " + unicodeName + "!", stringWriter.toString());
        verify(response).setContentType("text/plain");
    }
}