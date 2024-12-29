package com.example.smartypaws;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText currentPasswordInput, newPasswordInput, confirmPasswordInput;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        ImageButton backButton = findViewById(R.id.backButton);
        currentPasswordInput = findViewById(R.id.currentPasswordInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        MaterialButton changePasswordButton = findViewById(R.id.changePasswordButton);

        // Set click listeners
        backButton.setOnClickListener(v -> finish());
        changePasswordButton.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String currentPassword = currentPasswordInput.getText().toString().trim();
        String newPassword = newPasswordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword)) {
            currentPasswordInput.setError("Current password is required");
            return;
        }
        if (TextUtils.isEmpty(newPassword)) {
            newPasswordInput.setError("New password is required");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordInput.setError("Please confirm your new password");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            return;
        }
        if (newPassword.length() < 6) {
            newPasswordInput.setError("Password must be at least 6 characters");
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            // Re-authenticate the user
            mAuth.signInWithEmailAndPassword(user.getEmail(), currentPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Update password in Firebase Authentication
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            // Store the new password in Firestore
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            db.collection("users").document(user.getUid())
                                                    .update("password", newPassword)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Toast.makeText(this, "Password changed and stored successfully.", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(this, "Password changed but failed to store in Firestore.", Toast.LENGTH_SHORT).show();
                                                    });
                                        } else {
                                            Toast.makeText(this, "Failed to change password. Please try again.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            currentPasswordInput.setError("Incorrect current password");
                        }
                    });
        } else {
            Toast.makeText(this, "User not authenticated. Please log in again.", Toast.LENGTH_SHORT).show();
        }
    }
}
