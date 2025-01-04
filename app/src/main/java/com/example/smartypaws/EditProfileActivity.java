package com.example.smartypaws;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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

        // Initialize Firebase
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

        // Setup listeners
        setupLocationInput(); // Add custom functionality for location selection
        setupClickListeners();

        // Load profile data
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

    private void setupLocationInput() {
        locationInput.setOnClickListener(v -> {
            // Get the list of sorted countries
            String[] countries = CountryStateData.getCountries();

            // Show country selection dialog
            new AlertDialog.Builder(this)
                    .setTitle("Select Country")
                    .setItems(countries, (dialog, which) -> {
                        String selectedCountry = countries[which];

                        // Get the sorted states for the selected country
                        String[] states = CountryStateData.getStates(selectedCountry);

                        // Show state selection dialog if states are available
                        if (states.length > 0) {
                            new AlertDialog.Builder(this)
                                    .setTitle("Select State")
                                    .setItems(states, (stateDialog, stateWhich) -> {
                                        String selectedState = states[stateWhich];
                                        // Combine state and country
                                        locationInput.setText(String.format("%s, %s", selectedState, selectedCountry));
                                    })
                                    .show();
                        } else {
                            // No states available, just set the country
                            locationInput.setText(selectedCountry);
                        }
                    })
                    .show();
        });
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
                        String name = documentSnapshot.getString("name");
                        String location = documentSnapshot.getString("location");
                        String about = documentSnapshot.getString("about");

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

        if (displayName.isEmpty()) {
            displayNameInput.setError("Display name is required");
            return;
        }

        db.collection("users").document(currentUserId)
                .update("name", displayName, "location", location, "about", about)
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
