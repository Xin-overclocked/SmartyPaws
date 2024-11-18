package com.example.smartypaws;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpActivity extends AppCompatActivity {
    private TextInputEditText nameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton signUpButton;
    private MaterialButton googleSignInButton;
    private TextView loginLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signUpButton = findViewById(R.id.signUpButton);
        googleSignInButton = findViewById(R.id.googleSignInButton);
        loginLink = findViewById(R.id.loginLink);

        // Set up click listeners
        findViewById(R.id.loginLink).setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        googleSignInButton.setOnClickListener(v -> {
            // Implement Google Sign In
            Toast.makeText(this, "Google Sign In clicked", Toast.LENGTH_SHORT).show();
        });

        signUpButton.setOnClickListener(v -> performSignUp());
    }

    private void performSignUp() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Implement your sign up logic here
        // For example:
        // authViewModel.signUp(name, email, password);
    }
}