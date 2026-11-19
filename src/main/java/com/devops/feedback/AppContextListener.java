package com.devops.feedback;

import java.io.InputStream;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/** Creates the shared store at startup and preloads the sample dataset. */
@WebListener
public class AppContextListener implements ServletContextListener {

    public static final String STORE_ATTR = "feedbackStore";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        FeedbackStore store = new FeedbackStore();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("sample-feedback.csv")) {
            if (in != null) {
                int n = store.loadCsv(in);
                sce.getServletContext().log("Preloaded " + n + " feedback rows from sample-feedback.csv");
            }
        } catch (Exception e) {
            sce.getServletContext().log("Dataset not loaded: " + e.getMessage());
        }
        sce.getServletContext().setAttribute(STORE_ATTR, store);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().removeAttribute(STORE_ATTR);
    }
}
