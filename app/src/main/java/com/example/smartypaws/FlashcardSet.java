package com.example.smartypaws;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class FlashcardSet implements StudyItem{
    private String id;
    private String title;
    private String date;
    private Timestamp lastAccessed;
    private String description;
    private List<String> flashcardList;
    private String userid;

    // Default constructor required for calls to DataSnapshot.getValue(FlashcardSet.class)
    public FlashcardSet() {}

    public FlashcardSet(String title, String description, List<String> flashcardList, String date) {
        this.title = title;
        this.description = description;
        this.flashcardList = flashcardList;
        this.date = date;
    }

    public FlashcardSet(String title,String description) {
        this.title = title;
        this.description = description;
    }

    public FlashcardSet(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public FlashcardSet(String id, String title, String description, ArrayList<String> cardIds, String date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.flashcardList = cardIds;
        this.date = date;
    }

    public FlashcardSet(String id, String title, String description, ArrayList<String> cardIds, String date, String userid) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.flashcardList = cardIds;
        this.date = date;
        this.userid = userid;
    }

    // Getters and setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getDescription() { return description; }

    @Override
    public void setLastAccessed(Timestamp timestamp) {
        lastAccessed = timestamp;
    }

    @Override
    public Timestamp getLastAccessed() {
        return lastAccessed;
    }

    public void setDescription(String description) { this.description = description; }
    public List<String> getFlashcardList() { return flashcardList; }
    public void setFlashcards(List<String> flashcardList) { this.flashcardList = flashcardList; }
    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setId(String flashcardSetId) {
        id = flashcardSetId;
    }
}