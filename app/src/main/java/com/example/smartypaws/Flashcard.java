package com.example.smartypaws;

public class Flashcard {
    private String title;
    private String description;

    public Flashcard(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}