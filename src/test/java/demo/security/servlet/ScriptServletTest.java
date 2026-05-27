package demo.security.servlet;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScriptServletTest {

    @Test(expected = RuntimeException.class)
    public void doPost_rejectsDynamicScriptExecution() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getParameter("data")).thenReturn("alert(1)");

        new ScriptServlet().doPost(request, response);
    }
}
