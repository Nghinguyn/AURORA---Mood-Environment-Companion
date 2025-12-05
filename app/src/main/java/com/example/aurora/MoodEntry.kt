package com.example.aurora;

import java.io.Serializable;

public class MoodEntry implements Serializable {
    private String mood;
    private String description;
    private String date;

    // Constructor
    public MoodEntry(String mood, String description) {
        this.mood = mood;
        this.description = description;
        this.date = getCurrentDate();  // You can add the current date here
    }

    // Getters and Setters
    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    private String getCurrentDate() {
        // Get current date in a format like "MM/dd/yyyy"
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
        java.util.Date date = new java.util.Date();
        return sdf.format(date);
    }
}
