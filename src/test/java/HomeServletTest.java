import demo.security.servlet.HomeServlet;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for HomeServlet to ensure coverage and XSS prevention.
 */
class HomeServletTest {

    private void invokeDoGet(HomeServlet servlet, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Method doGet = HomeServlet.class.getDeclaredMethod("doGet", HttpServletRequest.class, HttpServletResponse.class);
        doGet.setAccessible(true);
        doGet.invoke(servlet, request, response);
    }

    private void invokeDoPost(HomeServlet servlet, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Method doPost = HomeServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);
    }

    @Test
    void testDoGetWithSafeName() throws Exception {
        HomeServlet servlet = new HomeServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(request.getParameter("name")).thenReturn("John");
        when(response.getWriter()).thenReturn(writer);

        invokeDoGet(servlet, request, response);

        verify(response).setContentType("text/html;charset=UTF-8");
        assertTrue(stringWriter.toString().contains("Hello John"));
    }

    @Test
    void testDoGetWithXSSAttempt() throws Exception {
        HomeServlet servlet = new HomeServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(request.getParameter("name")).thenReturn("<script>alert('XSS')</script>");
        when(response.getWriter()).thenReturn(writer);

        invokeDoGet(servlet, request, response);

        String output = stringWriter.toString();
        // OWASP encoder should escape the tags
        assertFalse(output.contains("<script>"), "XSS should be prevented");
        assertTrue(output.contains("&lt;") || output.contains("&amp;"), "Should contain escaped HTML");
    }

    @Test
    void testDoGetWithNullName() throws Exception {
        HomeServlet servlet = new HomeServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(request.getParameter("name")).thenReturn(null);
        when(response.getWriter()).thenReturn(writer);

        invokeDoGet(servlet, request, response);

        assertTrue(stringWriter.toString().contains("Hello Guest"));
    }

    @Test
    void testDoPost() throws Exception {
        HomeServlet servlet = new HomeServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(request.getParameter("name")).thenReturn("TestUser");
        when(response.getWriter()).thenReturn(writer);

        invokeDoPost(servlet, request, response);

        verify(response).setContentType("text/html;charset=UTF-8");
        assertTrue(stringWriter.toString().contains("Hello TestUser"));
    }
}

