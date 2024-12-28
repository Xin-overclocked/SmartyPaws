package com.example.smartypaws;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class FlashcardViewActivity extends AppCompatActivity {
    private LinearLayout flashcardsContainer;
    private String flashcardSetId;
    private String flashcardSetTitle;
    private String flashcardSetDescription;
    private ArrayList<String> cardsIds;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_view);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        flashcardsContainer = findViewById(R.id.flashcardsContainer);

        // Get flashcard id and title from the intent
        flashcardSetId = getIntent().getStringExtra("FLASHCARD_SET_ID");
        flashcardSetTitle = getIntent().getStringExtra("FLASHCARD_SET_TITLE");
        flashcardSetDescription = getIntent().getStringExtra("FLASHCARD_SET_DESCRIPTION");
        cardsIds = getIntent().getStringArrayListExtra("FLASHCARD_SET_CARDS");

        // Use the data to set up your view
        TextView titleTextView = findViewById(R.id.flashcardTitle);
        titleTextView.setText(flashcardSetTitle);
        TextView descriptionTextView = findViewById(R.id.flashcardDescription);
        descriptionTextView.setText(flashcardSetDescription);

        // Fetch and display flashcard data using the ID


        // Setup back button
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Setup menu button
        findViewById(R.id.menuButton).setOnClickListener(v -> showMenu());

        // Setup study button
        findViewById(R.id.studyButton).setOnClickListener(v -> startStudyMode());

        // Add flashcard pairs
//        addFlashcardPairs();
        addFlashcard();

    }

    private void addFlashcard() {
        // Initialize the HashMap for storing flashcard data
        HashMap<String, String> flashcardMap = new HashMap<>();

        // Assuming `cardIds` is already available (the list of card IDs you retrieved earlier)
        ArrayList<String> cardIds = cardsIds; // Replace this with your actual method to get card IDs

        // Check if cardIds is null or empty
        if (cardIds == null || cardIds.isEmpty()) {
            Log.e("FlashcardViewActivity", "Card IDs are null or empty");
            return; // Exit early if no card IDs are available
        }

        // Reference to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Loop through each card ID to retrieve its data
        for (String cardId : cardIds) {
            // Reference to the flashcard document using the card ID
            DocumentReference flashcardRef = db.collection("flashcards").document(cardId);
            System.out.println(cardId);

            // Fetch the flashcard document
            flashcardRef.get().addOnSuccessListener(flashcardSnapshot -> {
                if (flashcardSnapshot.exists()) {
                    // Get the term and definition from the document
                    String term = flashcardSnapshot.getString("term");
                    String definition = flashcardSnapshot.getString("definition");

                    // Add the term-definition pair to the HashMap
                    flashcardMap.put(term, definition);

                    // Now you can create and add the card to the UI (run on UI thread)
                    View cardPair = getLayoutInflater().inflate(R.layout.item_flashcard_pair, flashcardsContainer, false);
                    TextView termText = cardPair.findViewById(R.id.termText);
                    TextView definitionText = cardPair.findViewById(R.id.definitionText);

                    termText.setText(term);
                    definitionText.setText(definition);

                    // Add the card pair to the container
                    flashcardsContainer.addView(cardPair);
                }
            }).addOnFailureListener(e -> {
                Log.e("Firestore", "Error fetching flashcard data: ", e);
            });
        }
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