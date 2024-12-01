package com.example.smartypaws.PlayQuizFragment;

import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.smartypaws.R;
import org.jetbrains.annotations.NotNull;


public class QuizSettingFragment extends Fragment {

    EditText etNumberOfQuestions;
    TextView tvTotalQuestions;
    ImageButton btnPlay;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etNumberOfQuestions = view.findViewById(R.id.etNumberOfQuestions);
        tvTotalQuestions = view.findViewById(R.id.tvTotalQuestions);
        btnPlay = view.findViewById(R.id.btnPlay);

        startQuiz();
    }

    public void startQuiz() {
        btnPlay.setOnClickListener(v -> {
            String no = etNumberOfQuestions.getText().toString();
            if (no.isEmpty()) {
                Toast.makeText(getContext(), "Please enter the number of question", Toast.LENGTH_SHORT).show();
                return;
            }
            int numberOfQn = Integer.parseInt(no);



            //navigate to QuizPlayFragment
            NavController navController = Navigation.findNavController(requireView());

            Bundle bundle = new Bundle();
            bundle.putInt("noOfQn", numberOfQn);

            navController.navigate(R.id.destQuizPlay, bundle);
        });
    }


}