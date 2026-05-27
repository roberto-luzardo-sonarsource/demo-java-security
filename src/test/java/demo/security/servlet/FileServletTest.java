package demo.security.servlet;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileServletTest {

    @Test
    public void doPost_blocksPathTraversal() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getParameter("data")).thenReturn("../outside-tmp");

        assertThrows(SecurityException.class, () -> new FileServlet().doPost(request, response));
    }
}
