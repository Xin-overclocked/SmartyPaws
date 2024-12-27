package com.example.smartypaws;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FeedbackActivity extends AppCompatActivity {
    private EditText feedbackEditText;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback");

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

        // Get user email and current timestamp
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Create a feedback object
        HashMap<String, String> feedbackData = new HashMap<>();
        feedbackData.put("email", userEmail);
        feedbackData.put("feedback", feedback);
        feedbackData.put("timestamp", timestamp);

        // Push feedback to Firebase
        databaseReference.push().setValue(feedbackData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
                        feedbackEditText.setText(""); // Clear input
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to send feedback. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
