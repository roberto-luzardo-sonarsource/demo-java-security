package demo.security.servlet;

import demo.security.util.Utils;

import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/scripts")
public class ScriptServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String data = request.getParameter("data");
        try {
            Utils.executeJs(data);
        } catch (IllegalArgumentException | SecurityException e) {
            safeSendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            safeSendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Script execution error");
        }
    }

    private void safeSendError(HttpServletResponse response, int status, String msg) {
        try {
            response.sendError(status, msg);
        } catch (IOException ignored) {
            // best effort
        }
    }
}