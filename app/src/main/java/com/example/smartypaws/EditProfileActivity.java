package com.example.smartypaws;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private CircleImageView profileImage;
    private TextInputEditText displayNameInput;
    private TextInputEditText locationInput;
    private TextInputEditText aboutInput;
    private MaterialButton saveButton;
    private MaterialButton cancelButton;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String currentUserId;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        // Initialize Firebase first
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Check if user is logged in
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentUserId = auth.getCurrentUser().getUid();

        // Initialize views
        initializeViews();

        // Set click listeners
        setupClickListeners();

        // Load existing profile data
        loadProfileData();
    }

    private void initializeViews() {
        profileImage = findViewById(R.id.profileImage);
        displayNameInput = findViewById(R.id.displayNameInput);
        locationInput = findViewById(R.id.locationInput);
        aboutInput = findViewById(R.id.aboutInput);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
    }

    private void setupClickListeners() {
        profileImage.setOnClickListener(v -> openImagePicker());
        saveButton.setOnClickListener(v -> saveProfile());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void loadProfileData() {
        if (currentUserId == null) {
            Toast.makeText(this, "Error: User ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get data directly from document
                        String name = documentSnapshot.getString("name");
                        String location = documentSnapshot.getString("location");
                        String about = documentSnapshot.getString("about");

                        // Set data to views, checking for null
                        if (name != null) displayNameInput.setText(name);
                        if (location != null) locationInput.setText(location);
                        if (about != null) aboutInput.setText(about);
                    } else {
                        Toast.makeText(this, "Profile not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Error loading profile: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }

    private void saveProfile() {
        if (currentUserId == null) {
            Toast.makeText(this, "Error: User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String displayName = displayNameInput.getText() != null ?
                displayNameInput.getText().toString().trim() : "";
        String location = locationInput.getText() != null ?
                locationInput.getText().toString().trim() : "";
        String about = aboutInput.getText() != null ?
                aboutInput.getText().toString().trim() : "";

        // Validate inputs
        if (displayName.isEmpty()) {
            displayNameInput.setError("Display name is required");
            return;
        }

        // Create user profile data
        UserProfile userProfile = new UserProfile(displayName, location, about);

        // Save to Firebase
        db.collection("users").document(currentUserId)
                .set(userProfile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProfileActivity.this,
                            "Profile updated successfully",
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this,
                        "Error updating profile: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}