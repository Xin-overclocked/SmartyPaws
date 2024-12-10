package com.example.smartypaws;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuizViewActivity extends AppCompatActivity {
    private LinearLayout questionsContainer;
    private Quiz quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_view);

        // Initialize views
        questionsContainer = findViewById(R.id.questionsContainer);
        ImageButton backButton = findViewById(R.id.backButton);
        ImageButton menuButton = findViewById(R.id.menuButton);
        Button studyButton = findViewById(R.id.studyButton);

        // Set click listeners
        backButton.setOnClickListener(v -> finish());
        menuButton.setOnClickListener(v -> showOptionsMenu());
        studyButton.setOnClickListener(v -> startStudyMode());

        // Load quiz data
        loadQuizData();

        // Display quiz data
        displayQuizData();
    }

    private void loadQuizData() {
        // Simulating database data
        List<Quiz.Question> questions = new ArrayList<>();
        questions.add(new Quiz.Question("What is the capital of France?",
                List.of("London", "Berlin", "Paris", "Madrid"), 2));
        questions.add(new Quiz.Question("Which planet is known as the Red Planet?",
                List.of("Mars", "Venus", "Jupiter", "Saturn"), 0));
        questions.add(new Quiz.Question("Who painted the Mona Lisa?",
                List.of("Leonardo da Vinci", "Vincent van Gogh", "Pablo Picasso", "Michelangelo"), 0));

        quiz = new Quiz("MAD Lecture 2 Quiz", new Date(), "This is a quiz description.", questions);
    }

    private void displayQuizData() {
        // Set quiz title
        TextView quizTitleTextView = findViewById(R.id.quizTitleTextView);
        quizTitleTextView.setText(quiz.getTitle());

        // Set quiz date
        TextView quizDateTextView = findViewById(R.id.quizDateTextView);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        quizDateTextView.setText(dateFormat.format(quiz.getDate()));

        // Set quiz description
        TextView quizDescriptionTextView = findViewById(R.id.quizDescriptionTextView);
        quizDescriptionTextView.setText(quiz.getDescription());

        // Set questions count
        TextView questionsCountTextView = findViewById(R.id.questionsCountTextView);
        questionsCountTextView.setText(quiz.getQuestions().size() + " questions");

        // Add questions
        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            addQuestionView(i + 1, quiz.getQuestions().get(i));
        }
    }

    private void addQuestionView(int questionNumber, Quiz.Question question) {
        View questionView = getLayoutInflater().inflate(R.layout.item_question, questionsContainer, false);

        // Set question number
        TextView questionNumberTextView = questionView.findViewById(R.id.questionNumberTextView);
        questionNumberTextView.setText("Question " + questionNumber);

        // Get the options container
        LinearLayout optionsContainer = questionView.findViewById(R.id.optionsContainer);
        optionsContainer.removeAllViews(); // Clear any existing views

        // Add options dynamically
        for (String optionText : question.getOptions()) {
            Button optionButton = (Button) getLayoutInflater().inflate(R.layout.item_option, optionsContainer, false);
            optionButton.setText(optionText);
            optionButton.setOnClickListener(v -> handleOptionClick(optionButton));
            optionsContainer.addView(optionButton);
        }

        questionsContainer.addView(questionView);
    }

    private void handleOptionClick(Button option) {
        // TODO: Implement option selection logic
        Toast.makeText(this, "Selected: " + option.getText(), Toast.LENGTH_SHORT).show();
    }

    private void showOptionsMenu() {
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.menuButton));
        popup.getMenuInflater().inflate(R.menu.quiz_view_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_edit) {
                editQuiz();
                return true;
            }
            if (item.getItemId() == R.id.menu_share) {
                shareQuiz();
                return true;
            }
            if (item.getItemId() == R.id.menu_delete) {
                deleteQuiz();
                return true;
            }
            return true;
        });
        popup.show();
    }

    private void editQuiz() {
        // Implement edit functionality
        Toast.makeText(this, "Edit quiz", Toast.LENGTH_SHORT).show();
        Intent editIntent = new Intent(this, QuizEditActivity.class);
        editIntent.putExtra("QUIZ_ID", quiz.getId());
        startActivity(editIntent);
    }

    private void shareQuiz() {
        // Implement share functionality
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this quiz!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "I'm studying " + quiz.getTitle() + ". Join me!");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void deleteQuiz() {
        // Implement delete functionality
        new AlertDialog.Builder(this)
                .setTitle("Delete Quiz")
                .setMessage("Are you sure you want to delete this quiz?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Perform deletion from Firebase

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void startStudyMode() {
        // TODO: Implement study mode
        Toast.makeText(this, "Starting study mode...", Toast.LENGTH_SHORT).show();
    }
}

