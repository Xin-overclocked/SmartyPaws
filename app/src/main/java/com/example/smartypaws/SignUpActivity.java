package com.example.smartypaws;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartypaws.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignupBinding activitySignupBinding;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using Data Binding
        activitySignupBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(activitySignupBinding.getRoot());

        // Set up click listeners
        activitySignupBinding.loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        activitySignupBinding.googleSignInButton.setOnClickListener(v -> {
            // Implement Google Sign In
            Toast.makeText(this, "Google Sign In clicked", Toast.LENGTH_SHORT).show();
        });

        db = FirebaseFirestore.getInstance();

        activitySignupBinding.signUpButton.setOnClickListener(v -> setupAction());
    }

    private void setupAction() {
        String name = Objects.requireNonNull(activitySignupBinding.nameInput.getText()).toString().trim();
        String email = Objects.requireNonNull(activitySignupBinding.emailInput.getText()).toString().trim();
        String password = Objects.requireNonNull(activitySignupBinding.passwordInput.getText()).toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
        } else {
            performSignUp(name, email, password);
        }
    }

    private void performSignUp(String name, String email, String password) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("password", password);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(SignUpActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}