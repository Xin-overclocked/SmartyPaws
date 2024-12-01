package com.example.smartypaws.PlayQuizFragment;

import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.smartypaws.Entity.Question;
import com.example.smartypaws.R;
import com.google.android.material.button.MaterialButton;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class QuizPlayFragment extends Fragment {

    private TextView tvQn, tvProgress;
    private ImageButton btnPrev, btnNext;
    private MaterialButton btnOption1, btnOption2, btnOption3, btnOption4;
    private ProgressBar progressBar;
    private List<Question> qnList;
    private int curQnIndex = 0;
    private int score = 0;
    private boolean[] scores;
    private int noOfQn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz_play, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvQn = view.findViewById(R.id.tvQn);
        tvProgress = view.findViewById(R.id.tvProgress);
        btnPrev = view.findViewById(R.id.btnPrev);
        btnNext = view.findViewById(R.id.btnNext);
        btnOption1 = view.findViewById(R.id.btnOption1);
        btnOption2 = view.findViewById(R.id.btnOption2);
        btnOption3 = view.findViewById(R.id.btnOption3);
        btnOption4 = view.findViewById(R.id.btnOption4);
        progressBar = view.findViewById(R.id.progressBar);


        //get number of question from quiz setting fragment
        noOfQn = getNoOfQn();
        loadQn(noOfQn);
        displayQn();

        btnOption1.setOnClickListener(v -> {checkAns(0);});
        btnOption2.setOnClickListener(v -> {checkAns(1);});
        btnOption3.setOnClickListener(v -> {checkAns(2);});
        btnOption4.setOnClickListener(v -> {checkAns(3);});

        btnNext.setOnClickListener(v -> {nextQn();});
        btnPrev.setOnClickListener(v -> {prevQn();});
    }

    private int getNoOfQn() {
        if (getArguments() != null) {
            return getArguments().getInt("noOfQn", 0);
        } else {
            return 0;
        }
    }


    private void loadQn(int noOfQn) {

        qnList = new ArrayList<>();

        qnList.add(new Question("What is the capital of Malaysia?",
                new String[]{"Kuala Lumpur", "Jakarta", "Bangkok", "Manila"}, 0));
        qnList.add(new Question("Which planet is known as the Red Planet?",
                new String[]{"Earth", "Mars", "Venus", "Jupiter"}, 1));
        qnList.add(new Question("What is the largest mammal?",
                new String[]{"Elephant", "Blue Whale", "Giraffe", "Tiger"}, 1));

        scores = new boolean[noOfQn];
    }

    private void displayQn() {
        //display qn
        Question curQn = qnList.get(curQnIndex);
        tvQn.setText(curQn.getFront());
        btnOption1.setText(curQn.getOptions()[0]);
        btnOption2.setText(curQn.getOptions()[1]);
        btnOption3.setText(curQn.getOptions()[2]);
        btnOption4.setText(curQn.getOptions()[3]);

        //update progress
        tvProgress.setText((curQnIndex + 1) + "/" + qnList.size());
        progressBar.setProgress((curQnIndex + 1) * 100 / qnList.size());
    }

    private void checkAns(int selectedOptionIndex) {
        Question curQn = qnList.get(curQnIndex);
        if (selectedOptionIndex == curQn.getCorrectAnsIndex() && !scores[curQnIndex]) {
            scores[curQnIndex] = true;
            score++;
            Toast.makeText(getContext(), "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Wrong answer", Toast.LENGTH_SHORT).show();
        }
    }

    private void nextQn() {
        if (curQnIndex < qnList.size() - 1) {
            curQnIndex++;
            displayQn();
        } else {
            Toast.makeText(getContext(), "Quiz completed!" + score, Toast.LENGTH_SHORT).show();
            startScoreFragment();
        }
    }

    private void prevQn() {
        if (curQnIndex > 0) {
            curQnIndex--;
            displayQn();
        }
    }

    private void startScoreFragment() {
        NavController controller = Navigation.findNavController(requireView());

        Bundle bundle = new Bundle();
        bundle.putInt("score", score);
        bundle.putInt("noOfQn", noOfQn);
        controller.navigate(R.id.destScore, bundle);
    }
}