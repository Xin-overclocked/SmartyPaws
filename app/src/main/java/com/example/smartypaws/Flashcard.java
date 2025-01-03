package com.example.smartypaws;

public class Flashcard {
    private String id;
    private String term;
    private String definition;
    private String flashcardSetId;

    public Flashcard() {
    }

    public Flashcard(String setId, String term, String definition, String flashcardSetId) {
        this.id = setId;
        this.term = term;
        this.definition = definition;
        this.flashcardSetId = flashcardSetId;
    }

    public String getId() { return id; }
    public String getTerm() {
        return term;
    }

    public String getDefinition() {
        return definition;
    }

    public String getFlashcardSetId() { return flashcardSetId; }
}