package com.example.smartypaws;

import com.google.firebase.Timestamp;

import java.util.List;

public class Quiz implements StudyItem {
    private String id;  // Firebase key
    private String title;
    private String date;
    private Timestamp lastAccessed; // Timestamp
    private String description;
    private List<Question> questions;

    public Quiz(){}

    public Quiz(String id, String title, String date, String description, List<Question> questions) {
        this.id = id;  // Firebase key
        this.title = title;
        this.date = date;
        this.description = description;
        this.questions = questions;
    }

    public Quiz(String title, String description, String date, Timestamp lastAccessed, List<Question> questions) {
        this.title = title;
        this.date = date;
        this.lastAccessed = lastAccessed;
        this.description = description;
        this.questions = questions;
    }

    // Getters

    public String getId() { return id; }

    public String getTitle() { return title; }
    public String getDate() { return date; }
    public Timestamp getLastAccessed() { return lastAccessed; }
    public String getDescription() { return description; }
    public List<Question> getQuestions() { return questions; }

    // Setters
    public void setId(String id) {
        this.id = id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setLastAccessed(Timestamp timestamp) {
        this.lastAccessed = timestamp;
    }

    public static class Question {
        private String text;
        private String imageUrl;
        private String audioUrl;
        private List<Option> options;

        public static class Option {
            private String text;
            private String imageUrl;
            private String audioUrl;
            private boolean isCorrect;

            public Option(String text, boolean isCorrect, String imageUrl, String audioUrl) {
                this.text = text;
                this.isCorrect = isCorrect;
                this.imageUrl = imageUrl;
                this.audioUrl = audioUrl;
            }

            public String getText() { return text; }
            public String getImageUrl() { return imageUrl; }
            public String getAudioUrl() { return audioUrl; }
            public boolean isCorrect() { return isCorrect; }
        }

        public Question(String text, List<Option> options, String imageUrl, String audioUrl) {
            this.text = text;
            this.options = options;
            this.imageUrl = imageUrl;
            this.audioUrl = audioUrl;
        }

        // Getters
        public String getText() { return text; }
        public List<Option> getOptions() { return options; }
        public String getImageUrl() { return imageUrl; }
        public String getAudioUrl() { return audioUrl; }
    }
}
