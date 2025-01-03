package com.example.smartypaws.PlayQuizFragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.smartypaws.MainActivity;
import com.example.smartypaws.R;
import com.google.android.material.button.MaterialButton;
import org.jetbrains.annotations.NotNull;


public class ScoreFragment extends Fragment {

    private int correctAns, noOfQn;
    private double score;
    private TextView tvTotalQn, tvCorrectAns, tvScore;
    private MaterialButton btnRetake;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_score, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTotalQn = view.findViewById(R.id.tvTotalQn);
        tvCorrectAns = view.findViewById(R.id.tvCorrectAns);
        tvScore = view.findViewById(R.id.tvScore);

        btnRetake = view.findViewById(R.id.btnRetake);


        if (getArguments() != null) {
            correctAns = getArguments().getInt("score", 0);
            noOfQn = getArguments().getInt("noOfQn", 0);
            tvTotalQn.setText(noOfQn +  "");
            tvCorrectAns.setText(correctAns +"");

            score = ((double) correctAns / noOfQn) * 100.0;
            tvScore.setText((int) score + "%");

        }

        btnRetake.setOnClickListener(v -> {
            getActivity().finish();
        });

        view.findViewById(R.id.btnMainMenu).setOnClickListener(v-> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });


    }


}