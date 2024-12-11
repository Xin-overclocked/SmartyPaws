package com.example.smartypaws;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setupViews();
    }

    private void setupViews() {
        // Back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Leave a Review
        CardView leaveReviewCard = findViewById(R.id.leaveReviewCard);
        leaveReviewCard.setOnClickListener(v -> openPlayStoreReview());

        // Submit Feedback
        CardView submitFeedbackCard = findViewById(R.id.submitFeedbackCard);
        submitFeedbackCard.setOnClickListener(v -> openFeedbackForm());

        // Text Size
        CardView textSizeCard = findViewById(R.id.textSizeCard);
        textSizeCard.setOnClickListener(v -> showTextSizeDialog());

        // Change Password
        CardView changePasswordCard = findViewById(R.id.changePasswordCard);
        changePasswordCard.setOnClickListener(v -> openChangePassword());

        // Delete Account
        findViewById(R.id.deleteAccountButton).setOnClickListener(v -> showDeleteAccountDialog());
    }

    private void openPlayStoreReview() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            // If Play Store app is not available, open in browser
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
            startActivity(intent);
        }
    }

    private void openFeedbackForm() {
        // TODO: Implement feedback form activity
        Intent intent = new Intent(this, FeedbackActivity.class);
        startActivity(intent);
    }

    private void showTextSizeDialog() {
        String[] sizes = {"Small", "Medium", "Large"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Text Size")
                .setItems(sizes, (dialog, which) -> {
                    // TODO: Save selected text size preference
                    String selectedSize = sizes[which];
                    // Update app's text size
                })
                .show();
    }

    private void openChangePassword() {
        // TODO: Implement change password activity

    }

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // TODO: Implement account deletion
                    deleteAccount();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAccount() {
        // TODO: Implement account deletion logic
        // After successful deletion:
        // 1. Clear user data
        // 2. Sign out
        // 3. Return to login screen
    }
}

