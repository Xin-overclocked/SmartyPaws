package com.example.smartypaws.auth;

import android.util.Patterns;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class AuthService {
    // Listener Interface for Callbacks
    public interface OnAuthCompleteListener {
        void onSuccess(String message);
        void onFailure(String error);
    }

    private final FirebaseAuth firebaseAuth;

    public AuthService(){
        firebaseAuth = FirebaseAuth.getInstance();
    }

    //RegisterUser
    public void registerUser(String email, String password, OnAuthCompleteListener listener) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            listener.onFailure("Invalid email address");
            return;
        }
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onSuccess("Registration successful");
                    } else {
                        String errorMessage;
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            errorMessage = "Email already in use.";
                        } else {
                            errorMessage = "Registration failed: " + task.getException().getMessage();
                        }
                        listener.onFailure(errorMessage);
                    }
                    ;
                });
    }
    // loginuser
    public void loginUser(String email, String password, OnAuthCompleteListener listener) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onSuccess("Login successful");
                    } else {
                        String errorMessage;
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            errorMessage = "No account found with this email.";
                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            errorMessage = "Invalid password.";
                        } else {
                            errorMessage = "Login failed: " + task.getException().getMessage();
                        }
                        listener.onFailure(errorMessage);
                    }
                });
    }
    // logoutUser
    public void logoutUser(OnAuthCompleteListener listener) {
        try {
            firebaseAuth.signOut();
            listener.onSuccess("Logout successful");
        }catch (Exception e){
            listener.onFailure("Logout failed: "+ e.getMessage());
        }
    }
    // resetPassword
    public void resetPassword(String email, OnAuthCompleteListener listener) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            listener.onFailure("Invalid email address");
            return;
        }

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onSuccess("Password reset email sent");
                    } else {
                        listener.onFailure("Error: " + task.getException().getMessage());
                    }
                });
    }


}
