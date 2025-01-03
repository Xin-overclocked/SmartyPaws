package com.example.smartypaws.PlayQuizFragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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

import com.example.smartypaws.MainActivity;
import com.example.smartypaws.PlayQuizActivity;
import com.example.smartypaws.Quiz;
import com.example.smartypaws.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;


public class QuizPlayFragment extends Fragment {

    private TextView tvQn, tvProgress;
    private ImageButton btnPrev, btnNext;
    private MaterialButton btnOption1, btnOption2, btnOption3, btnOption4;

    private MaterialButton[] btns;
    private ProgressBar progressBar;

    private Quiz quiz;
    private List<Quiz.Question> qnList;
    private List<Quiz.Question.Option> curOptions;

    private int curQnIndex = 0;
    private int score = 0;
    private boolean[] scores;
    private int[] selectedBtnI;
    private int noOfQn;

    private FirebaseFirestore db;

    private MaterialButton selectedBtn;

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
        btns = new MaterialButton[]{btnOption1, btnOption2, btnOption3, btnOption4};

        db = FirebaseFirestore.getInstance();
        loadQuiz();

        ImageButton btnReturn = view.findViewById(R.id.btnReturn);

        btnReturn.findViewById(R.id.btnReturn).setOnClickListener(v-> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.destQuizSetting);
        });

        btnNext.setOnClickListener(v -> {
            nextQn();
        });
        btnPrev.setOnClickListener(v -> {
            prevQn();
        });
    }

    private void loadQuiz() {
        String quizId = ((PlayQuizActivity) requireActivity()).getQuizId();
        db.collection("quizzes").document(quizId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    quiz = documentSnapshot.toObject(Quiz.class);
                    if (quiz != null) {
                        qnList = quiz.getQuestions();
                        noOfQn = Math.min(getNoOfQn(), qnList.size());
                        loadQn(noOfQn);

                        displayQn();
                    } else {
                        Toast.makeText(getActivity(), "Failed to load quiz data", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error loading quiz " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private int getNoOfQn() {
        if (getArguments() != null) {
            return getArguments().getInt("noOfQn", 0);
        } else {
            return 0;
        }
    }


    private void loadQn(int noOfQn) {
        while (qnList.size() != noOfQn) {
            qnList.remove(qnList.size() - 1);
        }

        scores = new boolean[noOfQn];
        selectedBtnI = new int[noOfQn];
        Arrays.fill(selectedBtnI, -1);
    }

    private void displayQn() {
        btnOption3.setVisibility(View.INVISIBLE);
        btnOption4.setVisibility(View.INVISIBLE);

        setSelectedBtn();

        //display qn
        Quiz.Question curQn = qnList.get(curQnIndex);
        tvQn.setText(curQn.getText());

        curOptions = curQn.getOptions();

        btnOption1.setText(curOptions.get(0).getText());
        btnOption2.setText(curOptions.get(1).getText());

        if (curOptions.size() > 2) {
            btnOption3.setText(curOptions.get(2).getText());
        }

        if (curOptions.size() > 3) {
            btnOption4.setText(curOptions.get(3).getText());
        }

        for (int i = 0; i < curOptions.size(); i++) {
            int finalI = i;
            btns[i].setOnClickListener(v->{checkAns(finalI);});
            btns[i].setVisibility(View.VISIBLE);
        }


        //update progress
        tvProgress.setText((curQnIndex + 1) + "/" + noOfQn);
        progressBar.setProgress((curQnIndex + 1) * 100 / noOfQn);
    }


    private void setSelectedBtn() {
        if (selectedBtnI[curQnIndex] != -1) {
            btns[selectedBtnI[curQnIndex]].setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#553479")));

        }
        resetBtnAppearance();
    }

    private void resetBtnAppearance(){
        for (int i = 0; i < btns.length; i++) {
            if (i != selectedBtnI[curQnIndex]) {
                btns[i].setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7948AE")));
            }
        }
    }

    private void checkAns(int selectedOptionIndex) {
        selectedBtnI[curQnIndex] = selectedOptionIndex;

        if (curOptions.get(selectedOptionIndex).isCorrect()) {
            scores[curQnIndex] = true;
        } else {
            scores[curQnIndex] = false;
        }

        setSelectedBtn();
    }

    private void nextQn() {
        if (curQnIndex < qnList.size() - 1) {

            curQnIndex++;
            displayQn();
        } else {
            for (int i = 0; i < scores.length; i++) {
                if (scores[i]) {
                    score++;
                }
            }

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

        Toast.makeText(this.getActivity(), "score: " + score + " / " + noOfQn, Toast.LENGTH_SHORT).show();
        Bundle bundle = new Bundle();
        bundle.putInt("score", score);
        bundle.putInt("noOfQn", noOfQn);
        controller.navigate(R.id.destScore, bundle);
    }
}