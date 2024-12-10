package com.example.smartypaws;

import java.util.List;
import java.util.Date;

public class Quiz {
    private String id;  // Firebase key
    private String title;
    private Date date;
    private String description;
    private List<Question> questions;

    public Quiz(String id, String title, Date date, String description, List<Question> questions) {
        this.id = id;  // Firebase key
        this.title = title;
        this.date = date;
        this.description = description;
        this.questions = questions;
    }

    public Quiz(String title, Date date, String description, List<Question> questions) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.questions = questions;
    }

    // Getters

    public String getId() { return id; }

    public String getTitle() { return title; }
    public Date getDate() { return date; }
    public String getDescription() { return description; }
    public List<Question> getQuestions() { return questions; }

    public static class Question {
        private String text;
        private List<String> options;
        private int correctAnsIndex;

        public Question(String text, List<String> options, int correctAnsIndex) {
            this.text = text;
            this.options = options;
            this.correctAnsIndex = correctAnsIndex;
        }

        // Getters
        public String getText() { return text; }
        public List<String> getOptions() { return options; }
        public int getCorrectAnsIndex() { return correctAnsIndex; }
    }
}
