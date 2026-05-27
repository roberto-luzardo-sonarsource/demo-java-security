package demo.security.servlet;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScriptServletTest {

    @Test
    public void doPost_rejectsDynamicScriptExecution() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getParameter("data")).thenReturn("alert(1)");

        assertThrows(RuntimeException.class, () -> new ScriptServlet().doPost(request, response));
    }
}
