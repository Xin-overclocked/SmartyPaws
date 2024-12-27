package com.example.smartypaws;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class FeedbackActivity extends AppCompatActivity {
    private EditText feedbackEditText;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Initialize Firebase Firestore and Auth
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        feedbackEditText = findViewById(R.id.feedbackEditText);
        Button submitButton = findViewById(R.id.setButton);
        ImageButton backButton = findViewById(R.id.backButton);

        // Set click listeners
        backButton.setOnClickListener(v -> finish());
        submitButton.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        String feedback = feedbackEditText.getText().toString().trim();

        if (feedback.isEmpty()) {
            Toast.makeText(this, "Please enter your feedback", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current user's email and the current timestamp
        String userEmail = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : "Anonymous";
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Create a feedback object
        HashMap<String, Object> feedbackData = new HashMap<>();
        feedbackData.put("email", userEmail);
        feedbackData.put("feedback", feedback);
        feedbackData.put("timestamp", timestamp);

        // Save feedback to Firestore
        db.collection("feedbacks")
                .add(feedbackData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
                    feedbackEditText.setText(""); // Clear the input field
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to send feedback. Please try again.", Toast.LENGTH_SHORT).show());
    }
}
