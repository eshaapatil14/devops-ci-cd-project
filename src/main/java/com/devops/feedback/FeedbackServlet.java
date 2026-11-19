package com.devops.feedback;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** GET /feedback -> form + list ; POST /feedback -> submit ; GET /success -> confirmation. */
@WebServlet(urlPatterns = {"/feedback", "/success"})
public class FeedbackServlet extends HttpServlet {

    private FeedbackStore store(HttpServletRequest req) {
        return (FeedbackStore) getServletContext().getAttribute(AppContextListener.STORE_ATTR);
    }

    private void head(PrintWriter out, String title) {
        out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>" + title + "</title>");
        out.println("<style>body{font-family:Arial,sans-serif;max-width:800px;margin:30px auto;padding:0 15px}"
                + "input,textarea,select{width:100%;padding:8px;margin:4px 0 12px}"
                + "button{padding:10px 18px;background:#2563eb;color:#fff;border:0;border-radius:4px}"
                + "table{border-collapse:collapse;width:100%}td,th{border:1px solid #ddd;padding:6px;text-align:left}"
                + ".err{color:#b91c1c}</style></head><body>");
        out.println("<p><a href='" + "./'>Home</a> | <a href='feedback'>Feedback</a> | <a href='health'>Health</a></p>");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        if (req.getServletPath().equals("/success")) {
            head(out, "Thank you");
            out.println("<h2>Thank you!</h2><p>Your feedback was submitted successfully.</p>");
            out.println("<p><a href='feedback'>Submit another / view all</a></p></body></html>");
            return;
        }
        FeedbackStore s = store(req);
        head(out, "Feedback");
        out.println("<h2>Submit Feedback</h2>");
        if ("1".equals(req.getParameter("error"))) {
            out.println("<p class='err'>Invalid input. Please check all fields.</p>");
        }
        out.println("<form method='post' action='feedback'>"
                + "Name<input name='name' required>"
                + "Email<input name='email' type='email' required>"
                + "Feedback<textarea name='message' required></textarea>"
                + "Rating<select name='rating'><option>5</option><option>4</option><option>3</option>"
                + "<option>2</option><option>1</option></select>"
                + "<button type='submit'>Submit</button></form>");
        out.println("<h2>All Feedback (" + s.count() + ", avg rating " + s.averageRating() + ")</h2>");
        out.println("<table><tr><th>Name</th><th>Email</th><th>Feedback</th><th>Rating</th><th>Date</th></tr>");
        for (Feedback f : s.all()) {
            out.println("<tr><td>" + FeedbackStore.escape(f.getName()) + "</td><td>"
                    + FeedbackStore.escape(f.getEmail()) + "</td><td>"
                    + FeedbackStore.escape(f.getMessage()) + "</td><td>" + f.getRating()
                    + "</td><td>" + FeedbackStore.escape(f.getDate()) + "</td></tr>");
        }
        out.println("</table></body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        int rating;
        try {
            rating = Integer.parseInt(req.getParameter("rating"));
        } catch (NumberFormatException e) {
            rating = 0;
        }
        boolean ok = store(req).add(req.getParameter("name"), req.getParameter("email"),
                req.getParameter("message"), rating);
        resp.sendRedirect(ok ? "success" : "feedback?error=1");
    }
}
