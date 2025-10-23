package demo.security.servlet;

import demo.security.util.FileOperationException;
import demo.security.util.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@WebServlet("/files")
public class FileServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String data = request.getParameter("data");
        if (!Utils.isSafeFilename(data)) {
            safeSendError(response, HttpServletResponse.SC_BAD_REQUEST, "Filename required or invalid");
            return;
        }
    File baseDir = prepareBaseDir(response);
        if (baseDir == null) return; // error already sent

        File target = new File(baseDir, data);
        if (!isWithinBaseDir(baseDir, target)) {
            safeSendError(response, HttpServletResponse.SC_FORBIDDEN, "Path escape detected");
            return;
        }
        if (!target.exists()) {
            safeSendError(response, HttpServletResponse.SC_NOT_FOUND, "File not found");
            return;
        }
        try {
            Utils.deleteFile(data);
        } catch (FileOperationException se) {
            safeSendError(response, HttpServletResponse.SC_FORBIDDEN, se.getMessage());
            return;
        }
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    // Validation now centralized in Utils.isSafeFilename

    private File prepareBaseDir(HttpServletResponse response) {
        File baseDir = new File(System.getProperty("java.io.tmpdir"), "app-uploads");
        if (!baseDir.exists() && !baseDir.mkdirs()) {
            safeSendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not prepare directory");
            return null;
        }
        return baseDir;
    }

    private boolean isWithinBaseDir(File baseDir, File target) {
        try {
            String baseCanonical = baseDir.getCanonicalPath();
            String targetCanonical = target.getCanonicalPath();
            return targetCanonical.startsWith(baseCanonical + File.separator);
        } catch (IOException e) {
            return false;
        }
    }

    private void safeSendError(HttpServletResponse response, int status, String message) {
        try {
            response.sendError(status, message);
        } catch (IOException ignored) {
            // Best effort; nothing else we can do
        }
    }
}
