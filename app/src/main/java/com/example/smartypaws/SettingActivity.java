package com.example.smartypaws;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Set initial value of TVSelectedSize
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        String textSize = sharedPreferences.getString("TextSize", "Medium"); // Default to "Medium"

        TextView tvSelectedSize = findViewById(R.id.TVSelectedSize);
        if (tvSelectedSize != null) {
            tvSelectedSize.setText("Selected Size: " + textSize);
        }

        // Initialize the Log Out button
        Button logoutButton = findViewById(R.id.logoutButton);

        // Set OnClickListener for the Log Out button
        logoutButton.setOnClickListener(v -> logout());

        setupViews();
    }

    private void setupViews() {
        // Back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Notification Settings
        CardView notificationCard = findViewById(R.id.notificationCard);
        notificationCard.setOnClickListener(v -> openNotificationSettings());

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
        // TODO: Implement change password functionality
        /*CardView changePasswordCard = findViewById(R.id.changePasswordCard);
        changePasswordCard.setOnClickListener(v -> openChangePassword());*/

        // Delete Account
        findViewById(R.id.deleteAccountButton).setOnClickListener(v -> showDeleteAccountDialog());
    }

    private void openNotificationSettings(){
        // TODO: Implement notification settings activity
        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);


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

    /*private void showTextSizeDialog() {
        String[] sizes = {"Small", "Medium", "Large"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Text Size")
                .setItems(sizes, (dialog, which) -> {
                    // TODO: Save selected text size preference
                    String selectedSize = sizes[which];
                    // Update app's text size
                })
                .show();
    }*/
    private void showTextSizeDialog() {
        String[] sizes = {"Small", "Medium", "Large"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Text Size")
                .setItems(sizes, (dialog, which) -> {
                    String selectedSize = sizes[which];

                    // Save selected size in SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("TextSize", selectedSize);
                    editor.apply();

                    // Update the selected size TextView (TVSelectedSize)
                    TextView tvSelectedSize = findViewById(R.id.TVSelectedSize);
                    if (tvSelectedSize != null) {
                        tvSelectedSize.setText("Selected Size: " + selectedSize);
                    }

                    // Show a toast to confirm selection
                    Toast.makeText(this, "Text size set to " + selectedSize, Toast.LENGTH_SHORT).show();

                    recreate();
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
        Toast.makeText(this, "Account deleted successfully.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class); // Replace with your login activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // Method to handle log out logic
    private void logout() {
        // Clear user session data stored in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clears all saved preferences related to the session
        editor.apply();

        // Optionally, you can log the user out of any services (e.g., Firebase, etc.)
        // Example: FirebaseAuth.getInstance().signOut();

        // Show a Toast message confirming logout
        Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();

        // Redirect to the Login Activity after logout
        Intent intent = new Intent(this, LoginActivity.class); // Make sure LoginActivity exists
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Prevent going back to previous activity
        startActivity(intent);
        finish(); // Close the current activity (SettingActivity) to prevent returning to it
    }

}

