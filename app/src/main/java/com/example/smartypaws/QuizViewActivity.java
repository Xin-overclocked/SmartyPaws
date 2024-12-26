package com.example.smartypaws;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuizViewActivity extends AppCompatActivity {
    private LinearLayout questionsContainer;
    private Quiz quiz;
    private FirebaseFirestore db;
    private String quizId;
    private String quizTitle;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_view);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Get quiz ID from intent
        quizId = getIntent().getStringExtra("QUIZ_ID");

        // Initialize views
        questionsContainer = findViewById(R.id.questionsContainer);
        ImageButton backButton = findViewById(R.id.backButton);
        ImageButton menuButton = findViewById(R.id.menuButton);
        Button studyButton = findViewById(R.id.studyButton);

        // Set click listeners
        backButton.setOnClickListener(v -> finish());
        menuButton.setOnClickListener(this::showOptionsMenu);
        studyButton.setOnClickListener(v -> startStudyMode());

        // Load quiz data
        loadQuizData(quizId);
    }

    private void loadQuizData(String id) {
        db.collection("quizzes").document(quizId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    quiz = documentSnapshot.toObject(Quiz.class);
                    if (quiz != null) {
                        quiz.setId(id);
                        displayQuizData();
                        updateLastAccessed();
                    } else {
                        Toast.makeText(this, "Failed to load quiz data", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading quiz: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateLastAccessed() {
        db.collection("quizzes").document(quizId)
                .update("lastAccessed", Timestamp.now())
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update last accessed time", Toast.LENGTH_SHORT).show();
                });
    }

    private void displayQuizData() {
        // Set quiz title
        TextView quizTitleTextView = findViewById(R.id.quizTitleTextView);
        quizTitleTextView.setText(quiz.getTitle());

        // Set quiz date
        TextView quizDateTextView = findViewById(R.id.quizDateTextView);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        String createdAt = quiz.getCreatedAt(); // Assuming this is a String
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        try {
            Date date = inputDateFormat.parse(createdAt);
            quizDateTextView.setText(dateFormat.format(date)); // Format to "dd MMMM yyyy"
        } catch (ParseException e) {
            e.printStackTrace();
            quizDateTextView.setText("Invalid Date");
        }


        // Set quiz description
        TextView quizDescriptionTextView = findViewById(R.id.quizDescriptionTextView);
        quizDescriptionTextView.setText(quiz.getDescription());

        // Set questions count
        TextView questionsCountTextView = findViewById(R.id.questionsCountTextView);
        questionsCountTextView.setText(quiz.getQuestions().size() + " questions");

        questionsContainer.removeAllViews();
        List<Quiz.Question> questions = quiz.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            Quiz.Question question = questions.get(i);
            addQuestionView(i + 1, question);
        }
    }

    private void addQuestionView(int questionNumber, Quiz.Question question) {
        View questionView = getLayoutInflater().inflate(R.layout.item_question, questionsContainer, false);

        // Set question text
        TextView questionTextView = questionView.findViewById(R.id.questionTextView);
        questionTextView.setText(question.getText());

        // Load question image if available
//        ImageView questionImageView = questionView.findViewById(R.id.questionImageView);
//        if (question.getImageUrl() != null && !question.getImageUrl().isEmpty()) {
//            questionImageView.setVisibility(View.VISIBLE);
//            Glide.with(this).load(question.getImageUrl()).into(questionImageView);
//        } else {
//            questionImageView.setVisibility(View.GONE);
//        }

        // Add audio button if available
//        Button audioButton = questionView.findViewById(R.id.audioButton);
//        if (question.getAudioUrl() != null && !question.getAudioUrl().isEmpty()) {
//            audioButton.setVisibility(View.VISIBLE);
//            audioButton.setOnClickListener(v -> playAudio(question.getAudioUrl()));
//        } else {
//            audioButton.setVisibility(View.GONE);
//        }

        // Get the options container
        LinearLayout optionsContainer = questionView.findViewById(R.id.optionsContainer);
        optionsContainer.removeAllViews(); // Clear any existing views

        // Add options dynamically
        for (Quiz.Question.Option option : question.getOptions()) {
            // Inflate the item_option layout (LinearLayout)
            View optionView = getLayoutInflater().inflate(R.layout.item_option, optionsContainer, false);

            // Get the button inside the inflated layout
            Button optionButton = optionView.findViewById(R.id.optionButton);
            optionButton.setText(option.getText());
            optionButton.setOnClickListener(v -> handleOptionClick(optionButton, option.isCorrect()));
            optionsContainer.addView(optionView);

            // Get the option image view inside the inflated layout
//            ImageView optionImageView = optionView.findViewById(R.id.optionImageView);
//            if (option.getImageUrl() != null && !option.getImageUrl().isEmpty()) {
//                optionImageView.setVisibility(View.VISIBLE);
//                Glide.with(this).load(option.getImageUrl()).into(optionImageView);
//            } else {
//                optionImageView.setVisibility(View.GONE);
//            }

            // Add the audio button if needed
//            Button optionAudioButton = optionView.findViewById(R.id.optionAudioButton);
//            if (option.getAudioUrl() != null && !option.getAudioUrl().isEmpty()) {
//                optionAudioButton.setVisibility(View.VISIBLE);
//                optionAudioButton.setOnClickListener(v -> playAudio(option.getAudioUrl()));
//            } else {
//                optionAudioButton.setVisibility(View.GONE);
//            }
        }

        questionsContainer.addView(questionView);
    }

    private void handleOptionClick(Button option, boolean isCorrect) {
//        option.setBackgroundResource(isCorrect ? R.drawable.correct_option_background : R.drawable.incorrect_option_background);
        String message = isCorrect ? "Correct!" : "Incorrect. Try again.";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void playAudio(String audioUrl) {
        // TODO: Implement audio playback
        Toast.makeText(this, "Playing audio: " + audioUrl, Toast.LENGTH_SHORT).show();
    }

    private void showOptionsMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.quiz_view_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_edit) {
                editQuiz();
                return true;
//            }
//            else if (itemId == R.id.menu_share) {
//                shareQuiz();
//                return true;
            } else if (itemId == R.id.menu_delete) {
                deleteQuiz();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void editQuiz() {
        Intent editIntent = new Intent(this, QuizEditActivity.class);
        editIntent.putExtra("QUIZ_ID", quizId);
        startActivity(editIntent);
    }

    private void shareQuiz() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this quiz!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "I'm studying " + quiz.getTitle() + ". Join me!");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void deleteQuiz() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Quiz")
                .setMessage("Are you sure you want to delete this quiz?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    String currentUserId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";
                    if (quiz.getUserId().equals(currentUserId)) {
                        db.collection("quizzes").document(quizId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Quiz deleted successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error deleting quiz: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(this, "You don't have permission to delete this quiz", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void startStudyMode() {
//        Intent studyIntent = new Intent(this, QuizStudyActivity.class);
//        studyIntent.putExtra("QUIZ_ID", quizId);
//        startActivity(studyIntent);
    }
}

