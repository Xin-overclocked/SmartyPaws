package com.example.smartypaws;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartypaws.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding activityLoginBinding;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the binding
        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(activityLoginBinding.getRoot());

        // Set up click listener
        activityLoginBinding.signUpLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        db = FirebaseFirestore.getInstance();

        activityLoginBinding.loginButton.setOnClickListener(v -> performLogin());

        activityLoginBinding.googleSignInButton.setOnClickListener(v -> {
            // Implement Google Sign In
            Toast.makeText(this, "Google Sign In clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void performLogin() {
        String email = Objects.requireNonNull(activityLoginBinding.emailInput.getText()).toString().trim();
        String password = Objects.requireNonNull(activityLoginBinding.passwordInput.getText()).toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        } else {
            checkAccount(email, password);
        }

        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private void checkAccount(String email, String password) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            boolean isPasswordCorrect = false;

                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String storedPassword = document.getString("password");
                                if (storedPassword != null && storedPassword.equals(password)) {
                                    isPasswordCorrect = true;
                                    break;
                                }
                            }

                            if (isPasswordCorrect) {
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);

                            } else {
                                Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Account not found", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}