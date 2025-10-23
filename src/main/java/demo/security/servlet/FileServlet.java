package demo.security.servlet;

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
        if (data == null || data.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Filename required");
            return;
        }
        // Very restrictive: only allow deletion inside a safe upload directory
        if (data.contains("..") || data.startsWith("/") || data.startsWith("\\")) {
            try { response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid path"); } catch (IOException ignored) {
                // Intentionally ignored: best-effort to report error to client.
            }
            return;
        }
        // In a real app this base directory would be externalized
        File baseDir = new File(System.getProperty("java.io.tmpdir"), "app-uploads");
        if (!baseDir.exists() && !baseDir.mkdirs()) {
            try { response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not prepare directory"); } catch (IOException ignored) {
                // Intentionally ignored: cannot report, fall-through ends request.
            }
            return;
        }
        File target = new File(baseDir, data);
        if (!target.exists()) {
            try { response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found"); } catch (IOException ignored) {
                // Intentionally ignored: response already committed or client disconnected.
            }
            return;
        }
        Utils.deleteFile(target.getAbsolutePath());
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
