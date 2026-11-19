package com.devops.feedback;

/** Immutable feedback record. */
public final class Feedback {
    private final String name;
    private final String email;
    private final String message;
    private final int rating;
    private final String date;

    public Feedback(String name, String email, String message, int rating, String date) {
        this.name = name;
        this.email = email;
        this.message = message;
        this.rating = rating;
        this.date = date;
    }

    public String getName()    { return name; }
    public String getEmail()   { return email; }
    public String getMessage() { return message; }
    public int getRating()     { return rating; }
    public String getDate()    { return date; }
}
