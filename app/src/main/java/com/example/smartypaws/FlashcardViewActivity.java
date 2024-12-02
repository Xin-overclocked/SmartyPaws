package com.example.smartypaws;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

public class FlashcardViewActivity extends AppCompatActivity {
    private LinearLayout flashcardsContainer;

    private String flashcardSetId;
    private String flashcardSetTitle;
    private String flashcardSetDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_view);

        flashcardsContainer = findViewById(R.id.flashcardsContainer);

        // Get flashcard id and title from the intent
        flashcardSetId = getIntent().getStringExtra("FLASHCARD_SET_ID");
        flashcardSetTitle = getIntent().getStringExtra("FLASHCARD_SET_TITLE");
        flashcardSetDescription = getIntent().getStringExtra("FLASHCARD_SET_DESCRIPTION");

        // Use the data to set up your view
        TextView titleTextView = findViewById(R.id.flashcardTitle);
        titleTextView.setText(flashcardSetTitle);
        TextView descriptionTextView = findViewById(R.id.flashcardDescription);
        descriptionTextView.setText(flashcardSetDescription);

        // Fetch and display flashcard data using the ID
        //

        // Setup back button
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Setup menu button
        findViewById(R.id.menuButton).setOnClickListener(v -> showMenu());

        // Setup study button
        findViewById(R.id.studyButton).setOnClickListener(v -> startStudyMode());

        // Add flashcard pairs
        addFlashcardPairs();
    }

    private void addFlashcardPairs() {
        // Sample data - in a real app, this would come from a database or API
        String[][] flashcardData = {
                {"Term 1", "This is term 1's definition"},
                {"Term 2", "This is term 2's definition"},
                {"Term 3", "This is term 3's definition"}
        };

        for (String[] pair : flashcardData) {
            View cardPair = getLayoutInflater().inflate(R.layout.item_flashcard_pair, flashcardsContainer, false);

            TextView termText = cardPair.findViewById(R.id.termText);
            TextView definitionText = cardPair.findViewById(R.id.definitionText);

            termText.setText(pair[0]);
            definitionText.setText(pair[1]);

            flashcardsContainer.addView(cardPair);
        }
    }

    private void showMenu() {
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.menuButton));
        popup.getMenuInflater().inflate(R.menu.flashcard_view_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_edit) {
                editFlashcardSet();
                return true;
            }
            if (item.getItemId() == R.id.action_share) {
                shareFlashcardSet();
                return true;
            }
            if (item.getItemId() == R.id.action_delete) {
                deleteFlashcardSet();
                return true;
            }
            return true;
        });
        popup.show();
    }

    private void editFlashcardSet() {
        // Implement edit functionality
        Toast.makeText(this, "Edit flashcard set", Toast.LENGTH_SHORT).show();

        Intent editIntent = new Intent(this, FlashcardEditActivity.class);
//        editIntent.putExtra("FLASHCARD_SET_ID", flashcardSetId);
        startActivity(editIntent);
    }

    private void shareFlashcardSet() {
        // Implement share functionality
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this flashcard set!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "I'm studying " + flashcardSetTitle + ". Join me!");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void deleteFlashcardSet() {
        // Implement delete functionality
        new AlertDialog.Builder(this)
                .setTitle("Delete Flashcard Set")
                .setMessage("Are you sure you want to delete this flashcard set?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Perform deletion from Firebase

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void startStudyMode() {
        // Implementation for study mode
        Intent studyIntent = new Intent(this, FlashcardStudyActivity.class);
        studyIntent.putExtra("FLASHCARD_SET_ID", flashcardSetId);
        studyIntent.putExtra("FLASHCARD_SET_TITLE", flashcardSetTitle);
        startActivity(studyIntent);
    }
}