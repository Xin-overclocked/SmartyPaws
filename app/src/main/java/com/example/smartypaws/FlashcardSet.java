package com.example.smartypaws;

import java.util.List;

public class FlashcardSet {
    private String id;
    private String title;
    private String date;
    private String description;
    private List<Flashcard> flashcardList;

    // Default constructor required for calls to DataSnapshot.getValue(FlashcardSet.class)
    public FlashcardSet() {}

    public FlashcardSet(String title, String date, String description, List<Flashcard> flashcardList) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.flashcardList = flashcardList;
    }

    public FlashcardSet(String title,String description) {
        this.title = title;
        this.description = description;
    }

    // Getters and setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Flashcard> getFlashcardList() { return flashcardList; }
    public void setFlashcards(List<Flashcard> flashcardList) { this.flashcardList = flashcardList; }
}