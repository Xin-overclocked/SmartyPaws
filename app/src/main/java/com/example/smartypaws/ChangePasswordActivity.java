package com.example.smartypaws;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    // Declare input fields for current password, new password, and confirmation
    private TextInputEditText currentPasswordInput, newPasswordInput, confirmPasswordInput;

    // Firebase Authentication instance
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        ImageButton backButton = findViewById(R.id.backButton); // Back button to return to the previous screen
        currentPasswordInput = findViewById(R.id.currentPasswordInput); // Input for current password
        newPasswordInput = findViewById(R.id.newPasswordInput); // Input for new password
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput); // Input for confirming the new password
        MaterialButton changePasswordButton = findViewById(R.id.changePasswordButton); // Button to trigger password change

        // Set click listener for back button
        backButton.setOnClickListener(v -> finish());

        // Set click listener for change password button
        changePasswordButton.setOnClickListener(v -> changePassword());
    }

    /**
     * Method to handle password change logic
     */
    private void changePassword() {
        // Get user input from the text fields
        String currentPassword = currentPasswordInput.getText().toString().trim();
        String newPassword = newPasswordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validate user inputs
        if (TextUtils.isEmpty(currentPassword)) {
            currentPasswordInput.setError("Current password is required"); // Display error if current password is empty
            return;
        }
        if (TextUtils.isEmpty(newPassword)) {
            newPasswordInput.setError("New password is required"); // Display error if new password is empty
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordInput.setError("Please confirm your new password"); // Display error if confirm password is empty
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match"); // Display error if new password and confirmation do not match
            return;
        }
        if (newPassword.length() < 6) {
            newPasswordInput.setError("Password must be at least 6 characters"); // Enforce minimum password length
            return;
        }

        // Get the current authenticated user
        FirebaseUser user = mAuth.getCurrentUser();

        // Ensure the user is authenticated
        if (user != null && user.getEmail() != null) {
            // Create authentication credentials using the user's email and current password
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

            // Re-authenticate the user
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Update the password
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            // Notify the user of success
                                            Toast.makeText(this, "Password changed successfully.", Toast.LENGTH_SHORT).show();
                                            finish(); // Close the activity
                                        } else {
                                            // Notify the user of failure
                                            Toast.makeText(this, "Failed to change password. Please try again.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // Notify the user of incorrect current password
                            currentPasswordInput.setError("Incorrect current password");
                        }
                    });
        } else {
            // Notify the user if they are not authenticated
            Toast.makeText(this, "User not authenticated. Please log in again.", Toast.LENGTH_SHORT).show();
        }
    }
}
