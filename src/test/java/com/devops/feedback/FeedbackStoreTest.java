package com.devops.feedback;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class FeedbackStoreTest {

    @Test
    void addsValidFeedback() {
        FeedbackStore s = new FeedbackStore();
        assertTrue(s.add("Asha", "asha@example.com", "Great", 5));
        assertEquals(1, s.count());
    }

    @Test
    void rejectsInvalidEmail() {
        assertFalse(new FeedbackStore().add("Asha", "not-an-email", "Great", 5));
    }

    @Test
    void rejectsOutOfRangeRating() {
        FeedbackStore s = new FeedbackStore();
        assertFalse(s.add("Asha", "asha@example.com", "Great", 6));
        assertFalse(s.add("Asha", "asha@example.com", "Great", 0));
        assertEquals(0, s.count());
    }

    @Test
    void rejectsBlankFields() {
        FeedbackStore s = new FeedbackStore();
        assertFalse(s.add(" ", "a@b.com", "msg", 3));
        assertFalse(s.add("A", "a@b.com", "", 3));
    }

    @Test
    void computesAverageRating() {
        FeedbackStore s = new FeedbackStore();
        s.add("A", "a@b.com", "x", 5);
        s.add("B", "b@b.com", "y", 4);
        assertEquals(4.5, s.averageRating());
    }

    @Test
    void loadsCsvAndSkipsBadRows() throws Exception {
        String csv = "name,email,feedback,rating,date\n"
                + "Asha,asha@example.com,Great,5,2026-07-01\n"
                + "Bad,bad@example.com,Oops,notanumber,2026-07-02\n";
        FeedbackStore s = new FeedbackStore();
        int n = s.loadCsv(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));
        assertEquals(1, n);
        assertEquals(1, s.count());
    }

    @Test
    void escapesHtml() {
        assertEquals("&lt;script&gt;", FeedbackStore.escape("<script>"));
    }
}
