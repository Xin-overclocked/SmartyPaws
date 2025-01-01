package com.example.smartypaws;

import com.google.firebase.Timestamp;
import java.util.List;

public class Quiz implements StudyItem  {
    private String id;
    private String userId;
    private String title;
    private String description;
    private String createdAt;
    private Timestamp lastAccessed;
    private List<Question> questions;

    public Quiz() {
        // Default constructor required for Firestore
    }

    public Quiz(String userId, String title, String description, String createdAt, Timestamp lastAccessed, List<Question> questions) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.lastAccessed = lastAccessed;
        this.questions = questions;
    }

    public Quiz(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public Quiz(String id, String title, String description, Timestamp lastAccessed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.lastAccessed = lastAccessed;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public Timestamp getLastAccessed() { return lastAccessed; }
    public void setLastAccessed(Timestamp lastAccessed) { this.lastAccessed = lastAccessed; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }

    public static class Question {
        private String text;
        private List<Option> options;
//        private String imageUrl;
//        private String audioUrl;

        public Question() {
            // Default constructor required for Firestore
        }

        public Question(String text, List<Option> options, String imageUrl, String audioUrl) {
            this.text = text;
            this.options = options;
//            this.imageUrl = imageUrl;
//            this.audioUrl = audioUrl;
        }

        // Getters and setters
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }

        public List<Option> getOptions() { return options; }
        public void setOptions(List<Option> options) { this.options = options; }

//        public String getImageUrl() { return imageUrl; }
//        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
//
//        public String getAudioUrl() { return audioUrl; }
//        public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }

        public static class Option {
            private String text;
            private boolean isCorrect;
//            private String imageUrl;
//            private String audioUrl;

            public Option() {
                // Default constructor required for Firestore
            }

            public Option(String text, boolean isCorrect, String imageUrl, String audioUrl) {
                this.text = text;
                this.isCorrect = isCorrect;
//                this.imageUrl = imageUrl;
//                this.audioUrl = audioUrl;
            }

            // Getters and setters
            public String getText() { return text; }
            public void setText(String text) { this.text = text; }

            public boolean isCorrect() { return isCorrect; }
            public void setCorrect(boolean correct) { isCorrect = correct; }

//            public String getImageUrl() { return imageUrl; }
//            public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
//
//            public String getAudioUrl() { return audioUrl; }
//            public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
        }
    }
}
