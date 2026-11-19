package com.devops.feedback;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** GET /health -> 200 JSON when the app and its store are initialised, 503 otherwise. */
@WebServlet("/health")
public class HealthServlet extends HttpServlet {

    public static final String VERSION = "1.0.0";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        FeedbackStore s = (FeedbackStore) getServletContext().getAttribute(AppContextListener.STORE_ATTR);
        if (s == null) {
            resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            resp.getWriter().print("{\"status\":\"DOWN\"}");
            return;
        }
        resp.getWriter().print("{\"status\":\"UP\",\"version\":\"" + VERSION + "\",\"records\":" + s.count() + "}");
    }
}
