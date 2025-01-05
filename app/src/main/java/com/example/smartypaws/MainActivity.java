package com.example.smartypaws;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

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

import de.hdodenhof.circleimageview.CircleImageView;

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
    private CircleImageView profileImage;
    List<StudyItem> flashcardSetsList = new ArrayList<>();
    List<StudyItem> quizList = new ArrayList<>();

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
        // Setup Profile Picture
        profileImage = findViewById(R.id.profileImage);

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
                        for (QueryDocumentSnapshot document : snapshots) {
                            String title = document.getString("title");
                            String description = document.getString("description");
                            String id = document.getId();
                            ArrayList<String> cardIds = (ArrayList<String>) document.get("flashcardList");
                            String date = document.getString("date");
                            String userid = document.getString("userid");
                            Timestamp lastAccessed = document.getTimestamp("lastAccessed");
                            FlashcardSet flashcardSet = new FlashcardSet(id, title, description, cardIds, date, userid, lastAccessed);
                            flashcardSetsList.add(flashcardSet);
                        }
                        myFlashcardsAdapter.updateData(flashcardSetsList);
                        fetchRecentlyStudied();
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
                            Timestamp lastAccessed = document.getTimestamp("lastAccessed");
                            String id = document.getId();
                            Quiz quiz = new Quiz(id, title, description, lastAccessed);
                            quizList.add(quiz);
                        }
                        myQuizzesAdapter.updateData(quizList);
                        fetchRecentlyStudied();
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
                            setAvatar(name);
                        } else {
                            Log.e("MainActivity", "Name field is null or missing in Firestore");
                            setAvatar(null);
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
        List<StudyItem> recentlyStudiedList = new ArrayList<>();

        // Add all flashcards and quizzes to the list
        recentlyStudiedList.addAll(myFlashcardsAdapter.getItems());
        recentlyStudiedList.addAll(myQuizzesAdapter.getItems());

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

//    private void fetchFlashcards() {
//        flashcardSetsList.clear();
//        myFlashcardsAdapter.updateData(flashcardSetsList);
//        flashcardSetsRef
//                .whereEqualTo("userid", currentUserId)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            String title = document.getString("title");
//                            String description = document.getString("description");
//                            String id = document.getId();
//                            ArrayList<String> cardIds = (ArrayList<String>) document.get("flashcardList");
//                            String date = document.getString("date");
//                            String userid = document.getString("userid");
//                            Timestamp lastAccessed = document.getTimestamp("lastAccessed");
//                            FlashcardSet flashcardSet = new FlashcardSet(id, title, description, cardIds, date, userid, lastAccessed);
//                            flashcardSetsList.add(flashcardSet);
//                        }
//                        myFlashcardsAdapter.updateData(flashcardSetsList);
//                        fetchRecentlyStudied(); // Update the recently studied section after fetching flashcards
//                    } else {
//                        Toast.makeText(MainActivity.this, "Failed to load flashcards", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    private void fetchQuizzes() {
//        quizList.clear();
//        myQuizzesAdapter.updateData(new ArrayList<>());
//        quizzesRef
//                .whereEqualTo("userId", currentUserId)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        List<StudyItem> quizList = new ArrayList<>();
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            String title = document.getString("title");
//                            String description = document.getString("description");
//                            Timestamp lastAccessed = document.getTimestamp("lastAccessed");
//                            String id = document.getId();
//                            Quiz quiz = new Quiz(id, title, description, lastAccessed);
//                            quizList.add(quiz);
//                        }
//                        myQuizzesAdapter.updateData(quizList);
//                        fetchRecentlyStudied(); // Update the recently studied section after fetching quizzes
//                    } else {
//                        Toast.makeText(MainActivity.this, "Failed to load quizzes", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

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

    // Call this method to set the avatar based on the display name
    private void setAvatar(String displayName) {
        if (displayName == null || displayName.isEmpty()) {
            profileImage.setImageResource(R.drawable.profile_placeholder); // Default placeholder
            return;
        }

        // Get the first letter
        String firstLetter = displayName.substring(0, 1).toUpperCase();

        // Generate the avatar bitmap
        Bitmap avatarBitmap = createBitmapWithLetter(firstLetter);

        // Set the bitmap to the profileImage
        profileImage.setImageBitmap(avatarBitmap);
    }

    private Bitmap createBitmapWithLetter(String letter) {
        int size = 100; // Width and height of the avatar
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        // Draw the background and the letter
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        // Draw background
        paint.setColor(Color.parseColor("#553479"));
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);

        // Draw the letter
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.CENTER);

        // Get text bounds to center the letter
        Rect textBounds = new Rect();
        paint.getTextBounds(letter, 0, 1, textBounds);
        float x = size / 2;
        float y = size / 2 - textBounds.exactCenterY();

        canvas.drawText(letter, x, y, paint);

        return bitmap;
    }
}