package com.example.smartypaws;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recentlyStudiedRecyclerView;
    private RecyclerView myFlashcardsRecyclerView;
    private RecyclerView myQuizzesRecyclerView;
    private StudyItemAdapter recentlyStudiedAdapter;
    private StudyItemAdapter myFlashcardsAdapter;
    private StudyItemAdapter myQuizzesAdapter;

    private FirebaseFirestore db;
    private CollectionReference quizzesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();
        quizzesRef = db.collection("quizzes");

        // Set up RecyclerViews
        recentlyStudiedRecyclerView = findViewById(R.id.recentlyStudiedRecyclerView);
        myFlashcardsRecyclerView = findViewById(R.id.myFlashcardsRecyclerView);
        myQuizzesRecyclerView = findViewById(R.id.myQuizzesRecyclerView);

        recentlyStudiedRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        myFlashcardsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        myQuizzesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Sample data - replace with your actual data
        List<StudyItem> recentlyStudiedList = new ArrayList<>();
        List<StudyItem> myFlashcardsList = new ArrayList<>();
        List<StudyItem> myQuizzesList = new ArrayList<>();

        // Create adapters with click listeners
        recentlyStudiedAdapter = new StudyItemAdapter(recentlyStudiedList, this::navigateToView);
        myFlashcardsAdapter = new StudyItemAdapter(myFlashcardsList, this::navigateToView);
        myQuizzesAdapter = new StudyItemAdapter(myQuizzesList, this::navigateToView);

        recentlyStudiedRecyclerView.setAdapter(recentlyStudiedAdapter);
        myFlashcardsRecyclerView.setAdapter(myFlashcardsAdapter);
        myQuizzesRecyclerView.setAdapter(myQuizzesAdapter);

        // Fetch quizzes from Firestore
        fetchQuizzes();

        // Set up FAB click listener
        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a dialog instance
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.plus_btn_options);

                // Reference buttons inside the dialog
                Button createFlashcard = dialog.findViewById(R.id.btn_create_flashcard);
                Button createQuiz = dialog.findViewById(R.id.btn_create_quiz);

                // Set click listeners for the dialog buttons
                createFlashcard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Action for creating a new flashcard
                        startActivity(new Intent(getApplicationContext(), FlashcardEditActivity.class));
                        dialog.dismiss();

                    }
                });

                createQuiz.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Action for creating a new quiz
                        startActivity(new Intent(getApplicationContext(), QuizEditActivity.class));
                        dialog.dismiss();
                    }
                });

                // Show the dialog
                dialog.show();
            }
        });

        // Set up bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                finish();
                return true;
            }
            if (item.getItemId() == R.id.nav_home) {
                return true;
            }
            return false;
        });


    }

    private void fetchQuizzes() {
        quizzesRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<StudyItem> quizList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String description = document.getString("description");
                            String id = document.getId();
                            Quiz quiz = new Quiz(id, title, description);  // Assuming Quiz has this constructor
                            quizList.add(quiz);
                        }
                        // Update the quiz adapter with the fetched quizzes
                        myQuizzesAdapter.updateData(quizList);
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to load quizzes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToView(StudyItem studyItem) {
        if (studyItem instanceof FlashcardSet) {
            FlashcardSet flashcardSet = (FlashcardSet) studyItem;
            Intent intent = new Intent(MainActivity.this, FlashcardViewActivity.class);
            intent.putExtra("FLASHCARD_SET_ID", flashcardSet.getId()); // Assuming Flashcard has an getId() method
            intent.putExtra("FLASHCARD_SET_TITLE", flashcardSet.getTitle());
            intent.putExtra("FLASHCARD_SET_DESCRIPTION", flashcardSet.getDescription());
            startActivity(intent);
        }
        if (studyItem instanceof Quiz) {
            Quiz quiz = (Quiz) studyItem;
            Intent intent = new Intent(MainActivity.this, QuizViewActivity.class);
            intent.putExtra("QUIZ_ID", quiz.getId()); // Assuming Flashcard has an getId() method
            intent.putExtra("QUIZ_TITLE", quiz.getTitle());
            intent.putExtra("QUIZ_DESCRIPTION", quiz.getDescription());
            startActivity(intent);
        }
    }

    //Text Size
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(FontSizeContextWrapper.wrap(newBase));
    }
}