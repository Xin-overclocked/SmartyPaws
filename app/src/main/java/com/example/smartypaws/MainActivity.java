package com.example.smartypaws;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListenerRegistration flashcardsListener;
    private ListenerRegistration quizzesListener;
    private RecyclerView recentlyStudiedRecyclerView;
    private RecyclerView myFlashcardsRecyclerView;
    private RecyclerView myQuizzesRecyclerView;
    private StudyItemAdapter recentlyStudiedAdapter;
    private StudyItemAdapter myFlashcardsAdapter;
    private StudyItemAdapter myQuizzesAdapter;

    private FirebaseFirestore db;
    private CollectionReference quizzesRef;
    private CollectionReference flashcardSetsRef;
    private CollectionReference userRef;
    private FirebaseAuth auth;
    private String currentUserId;
    private TextView profileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        quizzesRef = db.collection("quizzes");
        flashcardSetsRef = db.collection("flashcardSet");
        userRef = db.collection("users");

        // Setup username
        profileName = findViewById(R.id.name);

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

        // Get the current user ID
        currentUserId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (currentUserId == null) {
            Log.e("MainActivity", "User ID is null. Ensure user is logged in.");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
        Log.e("currentuserid", currentUserId);

        // Fetch quizzes from Firestore
        setupRealtimeListeners();
        fetchRecentlyStudied();
//        fetchFlashcards();
//        fetchQuizzes();
        fetchRecentlyStudied();

        // Set up FAB click listener
        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> {
            // Create a dialog instance
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.plus_btn_options);

            // Reference buttons inside the dialog
            Button createFlashcard = dialog.findViewById(R.id.btn_create_flashcard);
            Button createQuiz = dialog.findViewById(R.id.btn_create_quiz);

            // Set click listeners for the dialog buttons
            createFlashcard.setOnClickListener(view -> {
                startActivity(new Intent(getApplicationContext(), FlashcardEditActivity.class));
                dialog.dismiss();
            });

            createQuiz.setOnClickListener(view -> {
                startActivity(new Intent(getApplicationContext(), QuizEditActivity.class));
                dialog.dismiss();
            });

            // Show the dialog
            dialog.show();
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
            return item.getItemId() == R.id.nav_home;
        });
        setProfileName();
    }

    private void setupRealtimeListeners() {
        flashcardsListener = flashcardSetsRef
                .whereEqualTo("userid", currentUserId)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e("MainActivity", "Listen failed for flashcards.", error);
                        Toast.makeText(MainActivity.this, "Failed to load flashcards", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null) {
                        List<StudyItem> flashcardSetsList = new ArrayList<>();
                        for (DocumentSnapshot document : snapshots) {
                            String title = document.getString("title");
                            String description = document.getString("description");
                            String id = document.getId();
                            ArrayList<String> cardIds = (ArrayList<String>) document.get("flashcardList");
                            String date = document.getString("date");
                            String userid = document.getString("userid");
                            FlashcardSet flashcardSet = new FlashcardSet(id, title, description, cardIds, date, userid);
                            flashcardSetsList.add(flashcardSet);
                        }
                        myFlashcardsAdapter.updateData(flashcardSetsList);
                    }
                });

        quizzesListener = quizzesRef
                .whereEqualTo("userId", currentUserId)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e("MainActivity", "Listen failed for quizzes.", error);
                        Toast.makeText(MainActivity.this, "Failed to load quizzes", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null) {
                        List<StudyItem> quizList = new ArrayList<>();
                        for (DocumentSnapshot document : snapshots) {
                            String title = document.getString("title");
                            String description = document.getString("description");
                            String id = document.getId();
                            Quiz quiz = new Quiz(id, title, description);
                            quizList.add(quiz);
                        }
                        myQuizzesAdapter.updateData(quizList);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove listeners when activity is destroyed
        if (flashcardsListener != null) {
            flashcardsListener.remove();
        }
        if (quizzesListener != null) {
            quizzesListener.remove();
        }
    }

    private void setProfileName() {
        userRef.document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        if (name != null) {
                            profileName.setText(name);

                        } else {
                            Log.e("MainActivity", "Name field is null or missing in Firestore");
                            profileName.setText("Unknown User");
                        }
                    } else {
                        Log.e("MainActivity", "User document does not exist");
                        Toast.makeText(MainActivity.this, "User profile not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MainActivity", "Error fetching profile: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "Error fetching profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchRecentlyStudied() {
        String userId = currentUserId;
        List<StudyItem> recentlyStudiedList = new ArrayList<>();

        // Query for recently studied quizzes
        db.collection("quizzes")
                .whereEqualTo("userId", userId)
                .orderBy("lastAccessed", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(quizSnapshots -> {
                    for (QueryDocumentSnapshot document : quizSnapshots) {
                        Quiz quiz = document.toObject(Quiz.class);
                        recentlyStudiedList.add(quiz);
                    }
                    fetchFlashcardsForRecentlyStudied(recentlyStudiedList);
                })
                .addOnFailureListener(e -> {
                    // If quizzes fail to load, proceed with fetching flashcards
                    fetchFlashcardsForRecentlyStudied(recentlyStudiedList);
                });
    }

    private void fetchFlashcardsForRecentlyStudied(List<StudyItem> recentlyStudiedList) {
        String userId = currentUserId;
        db.collection("flashcards")
                .whereEqualTo("userId", userId)
                .orderBy("lastAccessed", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(flashcardSnapshots -> {
                    for (QueryDocumentSnapshot document : flashcardSnapshots) {
                        FlashcardSet flashcardSet = document.toObject(FlashcardSet.class);
                        recentlyStudiedList.add(flashcardSet);
                    }
                    finalizeRecentlyStudiedList(recentlyStudiedList);
                })
                .addOnFailureListener(e -> {
                    // If flashcards fail to load, proceed with what we have
                    finalizeRecentlyStudiedList(recentlyStudiedList);
                });
    }

    private void finalizeRecentlyStudiedList(List<StudyItem> recentlyStudiedList) {
        if (recentlyStudiedList.isEmpty()) {
            // If both quizzes and flashcards failed to load or don't exist
            Toast.makeText(MainActivity.this, "No recently studied items found", Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", "No recently studied items found");
        } else {
            // Sort the combined list by lastAccessed, handling nulls
            recentlyStudiedList.sort((a, b) -> {
                Timestamp timeA = a.getLastAccessed();
                Timestamp timeB = b.getLastAccessed();
                if (timeA == null && timeB == null) return 0;
                if (timeA == null) return 1;
                if (timeB == null) return -1;
                return timeB.compareTo(timeA); // Descending order
            });

            // Limit to 5 items
            if (recentlyStudiedList.size() > 5) {
                recentlyStudiedList = recentlyStudiedList.subList(0, 5);
            }

            recentlyStudiedAdapter.updateData(recentlyStudiedList);
        }
    }


    private void fetchFlashcards() {
        flashcardSetsRef
                .whereEqualTo("userid", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<StudyItem> flashcardSetsList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String description = document.getString("description");
                            String id = document.getId();
                            ArrayList<String> cardIds = (ArrayList<String>) document.get("flashcardList");
                            String date = document.getString("date");
                            String userid = document.getString("userid");
                            FlashcardSet flashcardSet = new FlashcardSet(id, title, description, cardIds, date, userid);
                            flashcardSetsList.add(flashcardSet);
                        }
                        myFlashcardsAdapter.updateData(flashcardSetsList);
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to load flashcards", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchQuizzes() {
        quizzesRef
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<StudyItem> quizList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String description = document.getString("description");
                            String id = document.getId();
                            Quiz quiz = new Quiz(id, title, description);
                            quizList.add(quiz);
                        }
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
            ArrayList<String> flashcardList = (ArrayList<String>) flashcardSet.getFlashcardList();
            intent.putExtra("FLASHCARD_SET_CARDS", flashcardList);
            intent.putExtra("FLASHCARD_SET_CREATE", flashcardSet.getDate());
            intent.putExtra("FLASHCARD_SET_USERID", flashcardSet.getUserid());
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