package com.example.smartypaws;

public class Flashcard {
    private int id;
    private String term;
    private String definition;
    private String flashcardSetId;


    public Flashcard(String term, String definition) {
        this.term = term;
        this.definition = definition;
    }

    public int getId() { return id; }
    public String getTerm() {
        return term;
    }

    public String getDefinition() {
        return definition;
    }

    public String getFlashcardSetId() { return flashcardSetId; }
}