package com.example.smartypaws;

import java.util.List;

public class FlashcardSet {
    private String title;
    private String date;
    private String description;
    private List<Flashcard> flashcards;

    // Default constructor required for calls to DataSnapshot.getValue(FlashcardSet.class)
    public FlashcardSet() {}

    public FlashcardSet(String title, String date, String description, List<Flashcard> flashcards) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.flashcards = flashcards;
    }

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Flashcard> getFlashcards() { return flashcards; }
    public void setFlashcards(List<Flashcard> flashcards) { this.flashcards = flashcards; }
}