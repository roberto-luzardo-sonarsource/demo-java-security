import demo.security.servlet.FileServlet;
import org.junit.Test;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

import static org.mockito.Mockito.*;

public class FileServletTest {

    private static class ExposedFileServlet extends FileServlet {
        public void invokeDoPost(HttpServletRequest req, HttpServletResponse resp) throws Exception {
            super.doPost(req, resp);
        }
    }

    @Test
    public void testRejectsInvalidPathTraversal() throws Exception {
        ExposedFileServlet servlet = new ExposedFileServlet();
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(req.getParameter("data")).thenReturn("../etc/passwd");
        servlet.invokeDoPost(req, resp);
        verify(resp, atLeastOnce()).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
    }

    @Test
    public void testDeletesExistingFile() throws Exception {
        ExposedFileServlet servlet = new ExposedFileServlet();
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        File baseDir = new File(System.getProperty("java.io.tmpdir"), "app-uploads");
        baseDir.mkdirs();
        File target = new File(baseDir, "test.txt");
        if (!target.exists()) {
            target.createNewFile();
        }
        when(req.getParameter("data")).thenReturn("test.txt");
        servlet.invokeDoPost(req, resp);
        verify(resp, never()).sendError(anyInt(), anyString());
    }
}
