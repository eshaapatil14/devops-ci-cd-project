package com.devops.feedback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Simple in-memory storage (sufficient for the CI/CD demo). */
public class FeedbackStore {

    private final List<Feedback> items = new CopyOnWriteArrayList<>();

    /** Validates and adds a record. Returns true if accepted. */
    public boolean add(String name, String email, String message, int rating) {
        if (!isValid(name, email, message, rating)) {
            return false;
        }
        items.add(new Feedback(name.trim(), email.trim(), message.trim(), rating,
                LocalDate.now().toString()));
        return true;
    }

    public static boolean isValid(String name, String email, String message, int rating) {
        if (name == null || name.trim().isEmpty()) return false;
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) return false;
        if (message == null || message.trim().isEmpty()) return false;
        return rating >= 1 && rating <= 5;
    }

    public List<Feedback> all() {
        return Collections.unmodifiableList(new ArrayList<>(items));
    }

    public int count() {
        return items.size();
    }

    public double averageRating() {
        if (items.isEmpty()) return 0.0;
        double sum = 0;
        for (Feedback f : items) sum += f.getRating();
        return Math.round((sum / items.size()) * 10.0) / 10.0;
    }

    /** Loads CSV with header: name,email,feedback,rating,date. Returns rows loaded. */
    public int loadCsv(InputStream in) throws IOException {
        int loaded = 0;
        try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line = r.readLine(); // header
            while ((line = r.readLine()) != null) {
                String[] p = line.split(",", 5);
                if (p.length < 5) continue;
                try {
                    items.add(new Feedback(p[0].trim(), p[1].trim(), p[2].trim(),
                            Integer.parseInt(p[3].trim()), p[4].trim()));
                    loaded++;
                } catch (NumberFormatException ignored) {
                    // skip malformed row
                }
            }
        }
        return loaded;
    }

    /** Minimal HTML escaping to avoid XSS. */
    public static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
