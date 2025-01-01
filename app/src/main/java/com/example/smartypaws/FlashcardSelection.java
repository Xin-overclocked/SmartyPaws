package com.example.smartypaws;

import static android.util.Log.e;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class FlashcardSelection extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView myFlashcardsRecyclerView;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String currentUserId;
    private FlashcardAdapter flashcardAdapter;
    private CollectionReference flashcardSetsRef;
    private Button generateQuiz;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_selection);

        // Firebase initialization
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.myFlashcardsRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns grid

        // Load flashcardSets from Firebase
        loadFlashcardSets();

        // Set up generate quiz button
        generateQuiz = findViewById(R.id.generateQuizButton);
        generateQuiz.setOnClickListener(v -> {
            FlashcardSet selectedSet = flashcardAdapter.getSelectedFlashcardSet();

            if (selectedSet != null) {
                Log.d("FlashcardSetId", "Selected set ID: " + selectedSet.getId());
                fetchFlashcardsAndGenerateQuiz(selectedSet);
                Log.d("ButtonClick", "Generate Quiz button clicked");
            } else {
                Toast.makeText(this, "Please select a flashcard set", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }
    private void loadFlashcardSets() {
        db.collection("flashcardSet")
                .whereEqualTo("userid", currentUserId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<FlashcardSet> flashcardSets = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        FlashcardSet flashcardSet = doc.toObject(FlashcardSet.class);
                        if (flashcardSet != null) {
                            flashcardSets.add(flashcardSet);
                        }
                    }
                    flashcardAdapter = new FlashcardAdapter(flashcardSets);
                    recyclerView.setAdapter(flashcardAdapter);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load flashcard sets", Toast.LENGTH_SHORT).show());
    }

    private void fetchFlashcardsAndGenerateQuiz(FlashcardSet selectedSet) {
        db.collection("flashcards")
                .whereEqualTo("flashcardSetId", selectedSet.getId())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Flashcard> flashcards = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Flashcard flashcard = doc.toObject(Flashcard.class);
                        Log.d("FlashcardSetId", "Selected set ID: " + selectedSet.getId());
                        if (flashcard != null) {
                            flashcards.add(flashcard);
                        }
                    }
                    Log.d("query successfull", "query successfull set ID: " + selectedSet.getId());
                    if (!flashcards.isEmpty()) {
                        Quiz quiz = createQuiz(selectedSet, flashcards);
                        Log.d("Cretead QUIZZZ", "CREATED QUIZZZZ set ID: " + selectedSet.getId());
                        saveQuizToFirebase(quiz);
                    } else {
                        Toast.makeText(this, "No flashcards found in this set", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading flashcards", Toast.LENGTH_SHORT).show());
    }
    private Quiz createQuiz(FlashcardSet flashcardSet, List<Flashcard> flashcards) {
        String quizId = UUID.randomUUID().toString();
        String title = "Quiz for " + flashcardSet.getTitle();
        String userId = flashcardSet.getUserid();
        String description = "Quiz generated from Flashcard Set: " + flashcardSet.getDescription();

        List<Quiz.Question> questions = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String createdAt = dateFormat.format(new Date());

        for (Flashcard flashcard : flashcards) {
            String questionText = "What is the definition of: " + flashcard.getTerm();

            // Generate options only once per flashcard
            List<Quiz.Question.Option> options = generateOptions(flashcard.getDefinition(), flashcards);

            // Create the Question object
            Quiz.Question question = new Quiz.Question();
            question.setText(questionText);
            question.setOptions(options);

            // Add the question to the list
            questions.add(question);
        }

        // Create and populate the Quiz object
        Quiz quiz = new Quiz();
        quiz.setUserId(userId);
        quiz.setTitle(title);
        quiz.setDescription(description);
        quiz.setCreatedAt(createdAt);
        quiz.setLastAccessed(Timestamp.now());
        quiz.setQuestions(questions);

        return quiz;
    }

    private List<Quiz.Question.Option> generateOptions(String correctAnswer, List<Flashcard> flashcards) {
        List<Quiz.Question.Option> options = new ArrayList<>();

        // Create the correct option
        Quiz.Question.Option correctOption = new Quiz.Question.Option();
        correctOption.setCorrect(true);
        correctOption.setText(correctAnswer);
        options.add(correctOption);

        // Pre-filter unique incorrect answers
        List<String> incorrectAnswers = new ArrayList<>();
        for (Flashcard flashcard : flashcards) {
            if (!flashcard.getDefinition().equals(correctAnswer)) {
                incorrectAnswers.add(flashcard.getDefinition());
            }
        }

        // Shuffle once to randomize
        Collections.shuffle(incorrectAnswers);

        // Add the first 3 incorrect answers (or fewer if not enough)
        for (int i = 0; i < Math.min(3, incorrectAnswers.size()); i++) {
            Quiz.Question.Option wrongOption = new Quiz.Question.Option();
            wrongOption.setCorrect(false);
            wrongOption.setText(incorrectAnswers.get(i));
            options.add(wrongOption);
        }

        // Shuffle final options
        Collections.shuffle(options);

        return options;
    }

    private void saveQuizToFirebase(Quiz quiz) {
        // Add quiz to Firestore
        db.collection("quizzes")
                .add(quiz) // Add the quiz first
                .addOnSuccessListener(documentReference -> {
                    String quizId = documentReference.getId();
                    quiz.setId(quizId); // Set ID to quiz object for consistency

                    // Update the quiz document with the new ID
                    db.collection("quizzes").document(quizId)
                            .set(quiz)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("FirebaseSuccess", "Quiz created successfully with ID: " + quizId);
                                Toast.makeText(this, "Quiz created successfully!", Toast.LENGTH_SHORT).show();

                                // Navigate to QuizEditActivity
                                Intent intent = new Intent(this, QuizEditActivity.class);
                                intent.putExtra("QUIZ_ID", quizId);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("FirebaseError", "Failed to update quiz with ID: " + quizId, e);
                                Toast.makeText(this, "Error updating quiz with ID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseError", "Failed to save quiz: " + e.getMessage(), e);
                    Toast.makeText(this, "Error saving quiz: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
