package com.example.smartypaws;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

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

//    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView forgotPassword;
    private static final int RC_SIGN_IN = 9001;

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

//        db = FirebaseFirestore.getInstance();

        activityLoginBinding.loginButton.setOnClickListener(v -> performLogin());

        forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

//        activityLoginBinding.googleSignInButton.setOnClickListener(v -> {
//            signInWithGoogle();
//        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
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
//        db.collection("users")
//                .whereEqualTo("email", email)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
//                        QuerySnapshot querySnapshot = task.getResult();
//                        boolean isPasswordCorrect = false;
//
//                        for (QueryDocumentSnapshot document : querySnapshot) {
//                            String storedPassword = document.getString("password");
//                            if (storedPassword != null && storedPassword.equals(password)) {
//                                isPasswordCorrect = true;
//                                break;
//                            }
//                        }
//
//                        if (isPasswordCorrect) {
//                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                            startActivity(intent);
//
//                        } else {
//                            Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(LoginActivity.this, "Account not found", Toast.LENGTH_SHORT).show();
//                    }
//
//                })
//                .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show());


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
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