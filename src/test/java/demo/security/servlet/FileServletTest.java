package demo.security.servlet;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileServletTest {

    @Test(expected = SecurityException.class)
    public void doPost_blocksPathTraversal() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getParameter("data")).thenReturn("../outside-tmp");

        new FileServlet().doPost(request, response);
    }

    @Test
    public void doPost_deletesFileWithinTempDirectory() throws Exception {
        Path tempFile = Files.createTempFile("file-servlet-test-", ".txt");
        String fileName = tempFile.getFileName().toString();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getParameter("data")).thenReturn(fileName);

        FileServlet servlet = new FileServlet();
        servlet.doPost(request, response);

        assertFalse(Files.exists(tempFile));
    }
}
