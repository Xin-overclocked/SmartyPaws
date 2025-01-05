package com.example.smartypaws;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartypaws.databinding.ActivityMainBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private BarChart barChart;
    private FloatingActionButton fab;
    private ImageButton settingButton;
    private CircleImageView profileImage;
    private Button editButton;
//    ActivityMainBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView profileName;
    private TextView location;
    private TextView quizCount;
    private TextView flashcardCount;
    private TextView about;
    private String currentUserId;
    private CollectionReference quizzesRef;
    private CollectionReference flashcardSetsRef;
    private CollectionReference userRef;
    private ListenerRegistration flashcardListener;
    private ListenerRegistration quizListener;

    private ListenerRegistration userListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase instances first
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        quizzesRef = db.collection("quizzes");
        flashcardSetsRef = db.collection("flashcardSet");
        userRef = db.collection("users");

        // Get currentUserId after Firebase initialization
        if (auth.getCurrentUser() == null) {
            // Handle the case where user is not logged in
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            // Redirect to login activity or handle appropriately
            finish();
            return;
        }

        currentUserId = auth.getCurrentUser().getUid();

        // Initialize views
        profileName = findViewById(R.id.profile_name);
        profileImage = findViewById(R.id.profileImage);
        location = findViewById(R.id.profile_location);
        about = findViewById(R.id.about_text);
        quizCount = findViewById(R.id.quiz_added);
        flashcardCount = findViewById(R.id.flashcards_count);
        fab = findViewById(R.id.fabAdd);

        setupFAB();

        // Set up setting button
        settingButton = findViewById(R.id.setting_button);
        settingButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, SettingActivity.class);
            startActivity(intent);
        });

        // Set up edit button
        editButton = findViewById(R.id.edit_button);
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Set up bottom navigation
        setupBottomNavigation();

        // Fetch user data and setup real-time counters
        fetchUserDataAndCounts();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                return true;
            }
            return item.getItemId() == R.id.nav_profile;
        });
    }

    private void fetchUserDataAndCounts() {
        if (currentUserId == null) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch user profile data
        userRef.document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Update profile information
                        String name = documentSnapshot.getString("name");
                        String userLocation = documentSnapshot.getString("location");
                        String aboutText = documentSnapshot.getString("about");

                        // Check for null values before setting text
                        if (name != null) {
                            profileName.setText(name);
                        }
                        if (userLocation != null) {
                            location.setText(userLocation);
                        }
                        if (aboutText != null) {
                            about.setText(aboutText);
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this,
                                "User profile not found",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this,
                            "Error fetching profile: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void setupRealtimeCounts() {
        if (currentUserId == null) {
            return;
        }

        // Setup real-time listener for flashcard sets
        userListener = userRef
                .whereEqualTo("userid", currentUserId)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(ProfileActivity.this,
                                "Error listening to flashcards: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null) {
                        flashcardCount.setText(String.valueOf(snapshots.size()));
                    }
                });


        // Setup real-time listener for flashcard sets
        flashcardListener = flashcardSetsRef
                .whereEqualTo("userid", currentUserId)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(ProfileActivity.this,
                                "Error listening to flashcards: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null) {
                        flashcardCount.setText(String.valueOf(snapshots.size()));
                    }
                });

        // Setup real-time listener for quizzes
        quizListener = quizzesRef
                .whereEqualTo("userId", currentUserId)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(ProfileActivity.this,
                                "Error listening to quizzes: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null) {
                        quizCount.setText(String.valueOf(snapshots.size()));
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupRealtimeUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Remove all listeners when activity is not visible
        removeListeners();
    }

    private void setupRealtimeUpdates() {
        if (currentUserId == null) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Setup real-time listener for user profile
        userListener = userRef.document(currentUserId)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        Toast.makeText(ProfileActivity.this,
                                "Error listening to profile updates: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        // Update profile information
                        String name = documentSnapshot.getString("name");
                        String userLocation = documentSnapshot.getString("location");
                        String aboutText = documentSnapshot.getString("about");

                        // Update UI with null checks
                        if (name != null) {
                            profileName.setText(name);
                            setAvatar(name);
                        }else{
                            setAvatar(null);
                        }
                        if (userLocation != null) {
                            location.setText(userLocation);
                        }
                        if (aboutText != null) {
                            about.setText(aboutText);
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this,
                                "User profile not found",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        // Setup real-time listener for flashcard sets
        flashcardListener = flashcardSetsRef
                .whereEqualTo("userid", currentUserId)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(ProfileActivity.this,
                                "Error listening to flashcards: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null) {
                        flashcardCount.setText(String.valueOf(snapshots.size()));
                    }
                });

        // Setup real-time listener for quizzes
        quizListener = quizzesRef
                .whereEqualTo("userId", currentUserId)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(ProfileActivity.this,
                                "Error listening to quizzes: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null) {
                        quizCount.setText(String.valueOf(snapshots.size()));
                    }
                });
    }

    private void removeListeners() {
        if (flashcardListener != null) {
            flashcardListener.remove();
        }
        if (quizListener != null) {
            quizListener.remove();
        }
        if (userListener != null) {
            userListener.remove();
        }
    }

    private void setupFAB() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(ProfileActivity.this);
                dialog.setContentView(R.layout.plus_btn_options);

                Button createFlashcard = dialog.findViewById(R.id.btn_create_flashcard);
                Button createQuiz = dialog.findViewById(R.id.btn_create_quiz);

                createFlashcard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), FlashcardEditActivity.class));
                        dialog.dismiss();
                    }
                });

                createQuiz.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), QuizEditActivity.class));
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
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