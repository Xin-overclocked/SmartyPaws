package com.example.smartypaws;

import android.annotation.SuppressLint;
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

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class FlashcardViewActivity extends AppCompatActivity {
    private LinearLayout flashcardsContainer;
    private String flashcardSetId;
    private String flashcardSetTitle;
    private String flashcardSetDescription;
    private ArrayList<String> cardsIds;
    private String createDat;
    private String flashcardSetUserId;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private HashMap<String, String> flashcardMap = new HashMap<>();

    @SuppressLint("SetTextI18n")
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
        createDat = getIntent().getStringExtra("FLASHCARD_SET_CREATE");
        flashcardSetUserId = getIntent().getStringExtra("FLASHCARD_SET_USERID");

        // Use the data to set up your view
        TextView titleTextView = findViewById(R.id.flashcardTitle);
        TextView dateText = findViewById(R.id.dateText);
        titleTextView.setText(flashcardSetTitle);
        TextView descriptionTextView = findViewById(R.id.flashcardDescription);
        descriptionTextView.setText(flashcardSetDescription);
        TextView noOfCards = findViewById(R.id.numberOfFlashcard);

        // Fetch and display flashcard data using the ID
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        String createdAt = createDat; // Assuming this is a String

        // Correct format to match "yyyy-MM-dd" (e.g., "2024-12-28")
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            // Parse the input date string
            Date date = inputDateFormat.parse(createdAt);
            // Format the date into the desired output format "dd MMMM yyyy"
            dateText.setText(dateFormat.format(date));
        } catch (ParseException e) {
            dateText.setText("Invalid Date");
        }


        // Set flashcard count
        noOfCards.setText(cardsIds.size() + " flashcards");

        // Setup back button
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Setup menu button
        findViewById(R.id.menuButton).setOnClickListener(v -> showMenu());

        // Setup study button
        findViewById(R.id.studyButton).setOnClickListener(v -> startStudyMode());

        // Add flashcard
        addFlashcard();

    }

//    private void addQuestionView(int questionNumber, Quiz.Question question) {
//        View questionView = getLayoutInflater().inflate(R.layout.item_question, flashcardsContainer, false);
//
//        // Set question text
//        TextView questionTextView = questionView.findViewById(R.id.questionTextView);
//        questionTextView.setText(question.getText());
//
//        // Get the options container
//        LinearLayout optionsContainer = questionView.findViewById(R.id.optionsContainer);
//        optionsContainer.removeAllViews(); // Clear any existing views
//
//        // Add options dynamically
//        for (Quiz.Question.Option option : question.getOptions()) {
//            // Inflate the item_option layout (LinearLayout)
//            View optionView = getLayoutInflater().inflate(R.layout.item_option, optionsContainer, false);
//
//            // Get the button inside the inflated layout
//            Button optionButton = optionView.findViewById(R.id.optionButton);
//            optionButton.setText(option.getText());
//            optionButton.setOnClickListener(v -> handleOptionClick(optionButton, option.isCorrect()));
//            optionsContainer.addView(optionView);
//        }
//
//        questionsContainer.addView(questionView);
//    }

    private void addFlashcard() {
        // Initialize the HashMap for storing flashcard data
//        HashMap<String, String> flashcardMap = new HashMap<>();

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
    private void updateLastAccessed() {
        db.collection("flashcardSet").document(flashcardSetId)
                .update("lastAccessed", Timestamp.now())
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update last accessed time", Toast.LENGTH_SHORT).show();
                });
    }

    private void showMenu() {
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.menuButton));
        popup.getMenuInflater().inflate(R.menu.flashcard_view_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_edit) {
                editFlashcardSet();
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
        startActivity(editIntent);
    }

    private void deleteFlashcardSet() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Quiz")
                .setMessage("Are you sure you want to delete this quiz?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    String currentUserId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";
                    if (flashcardSetUserId.equals(currentUserId)) {
                        db.collection("flashcardSet").document(flashcardSetId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "FlashcardSet deleted successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error deleting flashcardSet: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                        for(String cardid : cardsIds) {
                            db.collection("flashcards").document(cardid)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Card deleted successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error deleting card: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(this, "You don't have permission to delete this flashcardSet", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void startStudyMode() {
        // Implementation for study mode
        Intent studyIntent = new Intent(this, FlashcardStudyActivity.class);
        studyIntent.putExtra("FLASHCARD_SET_ID", flashcardSetId);
        studyIntent.putExtra("FLASHCARD_SET_TITLE", flashcardSetTitle);
        // Store the HashMap in a Bundle
        Bundle bundle = new Bundle();
        bundle.putSerializable("FLASHCARD_DATA", flashcardMap);  // Store the HashMap

        // Attach the Bundle to the Intent
        studyIntent.putExtras(bundle);
        startActivity(studyIntent);
    }
}