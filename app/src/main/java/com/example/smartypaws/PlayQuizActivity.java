package com.example.smartypaws;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.smartypaws.PlayQuizFragment.QuizSettingFragment;

public class PlayQuizActivity extends AppCompatActivity {
    private String quizId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_quiz);

        quizId = getIntent().getStringExtra("QUIZ_ID");


    }

    public String getQuizId() {
        return quizId;
    }
}