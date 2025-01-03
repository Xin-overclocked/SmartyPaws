package com.example.smartypaws;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
    // Add a map to store flashcard IDs with their content
    private HashMap<String, Map<String, String>> flashcardFullData = new HashMap<>();

    private TextView titleTextView;
    private TextView dateText;
    private TextView descriptionTextView;
    private TextView noOfCards;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_view);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        flashcardsContainer = findViewById(R.id.flashcardsContainer);

        // Get flashcard set ID from intent
        flashcardSetId = getIntent().getStringExtra("FLASHCARD_SET_ID");

        // Initialize views
        titleTextView = findViewById(R.id.flashcardTitle);
        dateText = findViewById(R.id.dateText);
        descriptionTextView = findViewById(R.id.flashcardDescription);
        noOfCards = findViewById(R.id.numberOfFlashcard);

        // Set up buttons
        findViewById(R.id.backButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        findViewById(R.id.menuButton).setOnClickListener(v -> showMenu());
        findViewById(R.id.studyButton).setOnClickListener(v -> startStudyMode());

        // Setup realtime updates
        setupRealtimeUpdates();
    }

    private void setupRealtimeUpdates() {
        db.collection("flashcardSet").document(flashcardSetId)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w("FlashcardView", "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        // Update flashcard set data
                        flashcardSetTitle = snapshot.getString("title");
                        flashcardSetDescription = snapshot.getString("description");
                        createDat = snapshot.getString("date");
                        cardsIds = (ArrayList<String>) snapshot.get("flashcardList");
                        flashcardSetUserId = snapshot.getString("userid");

                        // Update UI elements
                        titleTextView.setText(flashcardSetTitle);
                        descriptionTextView.setText(flashcardSetDescription);

                        // Format and set date
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        try {
                            Date date = inputDateFormat.parse(createDat);
                            dateText.setText(dateFormat.format(date));
                        } catch (ParseException ex) {
                            dateText.setText("Invalid Date");
                        }

                        if (cardsIds != null) {
                            noOfCards.setText(cardsIds.size() + " flashcards");
                            // Refresh flashcards
                            refreshFlashcards();
                        }
                    }
                });
    }

    private void refreshFlashcards() {
        // Clear existing data
        flashcardsContainer.removeAllViews();
        flashcardMap.clear();
        flashcardFullData.clear();

        // Load flashcards
        addFlashcard();
    }

    private void addFlashcard() {
        if (cardsIds == null || cardsIds.isEmpty()) {
            Log.e("FlashcardViewActivity", "Card IDs are null or empty");
            return;
        }

        for (String cardId : cardsIds) {
            DocumentReference flashcardRef = db.collection("flashcards").document(cardId);
            flashcardRef.get().addOnSuccessListener(flashcardSnapshot -> {
                if (flashcardSnapshot.exists()) {
                    String term = flashcardSnapshot.getString("term");
                    String definition = flashcardSnapshot.getString("definition");

                    // Create and add view
                    View cardPair = getLayoutInflater().inflate(R.layout.item_flashcard_pair, flashcardsContainer, false);
                    TextView termText = cardPair.findViewById(R.id.termText);
                    TextView definitionText = cardPair.findViewById(R.id.definitionText);

                    termText.setText(term);
                    definitionText.setText(definition);

                    // Store the flashcard ID in the view's tag
                    cardPair.setTag(cardId);

                    flashcardsContainer.addView(cardPair);

                    // Store both the simple map and full data
                    flashcardMap.put(term, definition);

                    Map<String, String> cardData = new HashMap<>();
                    cardData.put("term", term);
                    cardData.put("definition", definition);
                    flashcardFullData.put(cardId, cardData);
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
        Intent editIntent = new Intent(this, FlashcardEditActivity.class);
        editIntent.putExtra("FLASHCARDSET_ID", flashcardSetId);

        // Pass both the flashcard content and their IDs
        Bundle bundle = new Bundle();
        bundle.putSerializable("FLASHCARD_DATA", flashcardMap);
        bundle.putSerializable("FLASHCARD_FULL_DATA", flashcardFullData);
        bundle.putStringArrayList("CARD_IDS", cardsIds);

        editIntent.putExtras(bundle);
        startActivity(editIntent);
    }

    private void deleteFlashcardSet() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Flashcard Set")
                .setMessage("Are you sure you want to delete this flashcard set?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    String currentUserId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";
                    if (flashcardSetUserId.equals(currentUserId)) {
                        db.collection("flashcardSet").document(flashcardSetId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Flashcard set deleted successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error deleting flashcard set: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                        // Delete all associated flashcards
                        for(String cardId : cardsIds) {
                            db.collection("flashcards").document(cardId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Flashcard deleted successfully: " + cardId);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Error deleting flashcard: " + cardId, e);
                                    });
                        }
                    } else {
                        Toast.makeText(this, "You don't have permission to delete this flashcard set", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void startStudyMode() {
        Intent studyIntent = new Intent(this, FlashcardStudyActivity.class);
        studyIntent.putExtra("FLASHCARD_SET_ID", flashcardSetId);
        studyIntent.putExtra("FLASHCARD_SET_TITLE", flashcardSetTitle);

        Bundle bundle = new Bundle();
        bundle.putSerializable("FLASHCARD_DATA", flashcardMap);
        bundle.putSerializable("FLASHCARD_FULL_DATA", flashcardFullData);

        studyIntent.putExtras(bundle);
        startActivity(studyIntent);
    }
}