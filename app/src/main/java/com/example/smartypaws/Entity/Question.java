package com.example.smartypaws.Entity;

import java.util.List;

public class Question {

    private int questionID;
    private String front;
    private String [] options;
    private int correctAnsIndex;

    public Question(String front, String[] options, int correctAnsIndex) {
        this.front = front;
        this.options = options;
        this.correctAnsIndex = correctAnsIndex;
    }

    public int getQuestionID() {
        return questionID;
    }

    public String getFront() {
        return front;
    }

    public String[] getOptions() {
        return options;
    }

    public int getCorrectAnsIndex() {
        return correctAnsIndex;
    }
}
