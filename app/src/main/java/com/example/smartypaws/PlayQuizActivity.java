package com.example.smartypaws;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.example.smartypaws.PlayQuizFragment.QuizSettingFragment;

public class PlayQuizActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);


//        showQuizSettingFragment();
    }

    public void showQuizSettingFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.quiz_fragment_container, new QuizSettingFragment()).commit();
    }
}