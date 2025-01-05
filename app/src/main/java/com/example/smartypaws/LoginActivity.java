package com.example.smartypaws;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartypaws.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding activityLoginBinding;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView forgotPassword;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Inflate the binding
        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(activityLoginBinding.getRoot());

        // Set up click listener
        activityLoginBinding.signUpLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        activityLoginBinding.loginButton.setOnClickListener(v -> performLogin());

        forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null && currentUser.isEmailVerified()){
            updateUI(currentUser);
        }
    }

    private void performLogin() {
        String email = Objects.requireNonNull(activityLoginBinding.emailInput.getText()).toString().trim();
        String password = Objects.requireNonNull(activityLoginBinding.passwordInput.getText()).toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
        } else {
            checkAccount(email, password);
        }
    }

    private void checkAccount(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (user.isEmailVerified()) {
                                updateEmailVerificationStatus(user.getUid());
                                updateUI(user);
                            } else {
                                mAuth.signOut();
                                showVerificationDialog(user);
                            }
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showVerificationDialog(FirebaseUser user) {
        new AlertDialog.Builder(this)
                .setTitle("Email Not Verified")
                .setMessage("Please verify your email first. Would you like to resend the verification email?")
                .setPositiveButton("Resend", (dialog, which) -> {
                    // Get current user instance before sending verification email
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        sendVerificationEmail(currentUser);
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Please sign in again to resend verification email",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void sendVerificationEmail(FirebaseUser user) {
        long authTimestamp = Objects.requireNonNull(user.getMetadata()).getLastSignInTimestamp();
        long currentTime = System.currentTimeMillis();

        if (currentTime - authTimestamp > 30 * 60 * 1000) {
            // re-authenticate
            Toast.makeText(this, "Please sign in again to resend verification email",
                    Toast.LENGTH_LONG).show();
            mAuth.signOut();
            return;
        }

        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this,
                                "New verification email sent. Please check your inbox.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        String errorMessage = task.getException() != null ?
                                task.getException().getMessage() :
                                "Failed to send verification email.";
                        Toast.makeText(LoginActivity.this, errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateEmailVerificationStatus(String userId) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .update("emailVerified", true)
                .addOnFailureListener(e ->
                        Toast.makeText(LoginActivity.this,
                                "Error updating verification status",
                                Toast.LENGTH_SHORT).show());
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
