package com.example.smartypaws;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FlashcardEditActivity extends AppCompatActivity {
    private LinearLayout flashcardsContainer;
    private View lastClickedCard = null;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private String flashcardSetId;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private String currentUserId;
    private HashMap<String, String> flashcardMap;
    private HashMap<String, String> flashcardIds = new HashMap<>(); // Store flashcard IDs with their terms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_edit);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        // Initialize views
        flashcardsContainer = findViewById(R.id.flashcardsContainer);
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEdit);

        // Get data from intent
        flashcardSetId = getIntent().getStringExtra("FLASHCARDSET_ID");
        flashcardMap = (HashMap<String, String>) getIntent().getSerializableExtra("FLASHCARD_DATA");

        // Load existing data or add initial card
        if (flashcardSetId != null) {
            loadFlashCardSetData(flashcardSetId);
        } else {
            addFlashcard(null, "", "");
        }

        // Set up buttons
        findViewById(R.id.saveButton).setOnClickListener(v -> saveFlashcardSet());
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void loadFlashCardSetData(String flashcardSetId) {
        db.collection("flashcardSet").document(flashcardSetId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        FlashcardSet flashcardSet = documentSnapshot.toObject(FlashcardSet.class);
                        if (flashcardSet != null) {
                            titleEditText.setText(flashcardSet.getTitle());
                            descriptionEditText.setText(flashcardSet.getDescription());
                            loadFlashCards(flashcardSetId);
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading flashcard set: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadFlashCards(String flashcardSetId) {
        db.collection("flashcards")
                .whereEqualTo("flashcardSetId", flashcardSetId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    flashcardsContainer.removeAllViews();
                    for (DocumentSnapshot flashcardSnapshot : querySnapshot.getDocuments()) {
                        String flashcardId = flashcardSnapshot.getId();
                        String term = flashcardSnapshot.getString("term");
                        String definition = flashcardSnapshot.getString("definition");

                        if (term != null && definition != null) {
                            addFlashcard(flashcardId, term, definition);
                            flashcardIds.put(term, flashcardId); // Store the ID with its term
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading flashcards: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void addFlashcard(String flashcardId, String term, String definition) {
        View cardView = getLayoutInflater().inflate(R.layout.item_flashcard_edit, flashcardsContainer, false);

        // Set the flashcard ID as a tag
        cardView.setTag(flashcardId);

        EditText termEditText = cardView.findViewById(R.id.termEdit);
        EditText definitionEditText = cardView.findViewById(R.id.definitionEdit);

        termEditText.setText(term);
        definitionEditText.setText(definition);

        setupCardListeners(cardView);
        flashcardsContainer.addView(cardView);
    }

    private void setupCardListeners(View cardView) {
        CardView card = cardView.findViewById(R.id.cardView);
        LinearLayout buttonContainer = cardView.findViewById(R.id.buttonContainer);
        FloatingActionButton addButton = cardView.findViewById(R.id.addButton);
        FloatingActionButton deleteButton = cardView.findViewById(R.id.deleteButton);

        card.setOnClickListener(v -> {
            if (lastClickedCard != null && lastClickedCard != cardView) {
                lastClickedCard.findViewById(R.id.buttonContainer).setVisibility(View.GONE);
            }
            buttonContainer.setVisibility(buttonContainer.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            lastClickedCard = cardView;
        });

        addButton.setOnClickListener(v -> {
            addFlashcard(null, "", "");
            buttonContainer.setVisibility(View.GONE);
        });

        deleteButton.setOnClickListener(v -> {
            if (flashcardsContainer.getChildCount() > 1) {
                String flashcardId = (String) cardView.getTag();
                if (flashcardId != null) {
                    // Remove the ID from the flashcard set's list
                    db.collection("flashcardSet").document(flashcardSetId)
                            .update("flashcardList", FieldValue.arrayRemove(flashcardId));

                    // Delete the flashcard document
                    db.collection("flashcards").document(flashcardId).delete();
                }
                flashcardsContainer.removeView(cardView);
            }
        });

        setupMenuButtons(cardView);
    }

    private void setupMenuButtons(View cardView) {
        cardView.findViewById(R.id.termMenuButton).setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.inflate(R.menu.flashcard_edit_menu);
            popup.show();
        });

        cardView.findViewById(R.id.definitionMenuButton).setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.inflate(R.menu.flashcard_edit_menu);
            popup.show();
        });
    }

    @SuppressLint("SimpleDateFormat")
    private void saveFlashcardSet() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Title and description cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        FlashcardSet flashcardSet = new FlashcardSet();
        flashcardSet.setTitle(title);
        flashcardSet.setDescription(description);
        flashcardSet.setUserid(currentUserId);
        flashcardSet.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        flashcardSet.setLastAccessed(new Timestamp(new Date()));

        // Handle create or update
        DocumentReference setRef = (flashcardSetId == null) ?
                db.collection("flashcardSet").document() : db.collection("flashcardSet").document(flashcardSetId);

        flashcardSetId = setRef.getId();
        flashcardSet.setId(flashcardSetId);

        setRef.set(flashcardSet)
                .addOnSuccessListener(aVoid -> saveFlashcard(flashcardSetId, title))
                .addOnFailureListener(e -> Toast.makeText(this, "Error saving flashcard set: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveFlashcard(String flashcardSetId, String title) {
        List<Task<Void>> saveTasks = new ArrayList<>();

        for (int i = 0; i < flashcardsContainer.getChildCount(); i++) {
            View flashcardView = flashcardsContainer.getChildAt(i);
            EditText termEditText = flashcardView.findViewById(R.id.termEdit);
            EditText definitionEditText = flashcardView.findViewById(R.id.definitionEdit);

            String term = termEditText.getText().toString().trim();
            String definition = definitionEditText.getText().toString().trim();
            String flashcardId = (String) flashcardView.getTag();

            if (!term.isEmpty() && !definition.isEmpty()) {
                if (flashcardId == null) {
                    // This is a new flashcard
                    flashcardId = db.collection("flashcards").document().getId();
                    flashcardView.setTag(flashcardId);
                }

                Flashcard flashcard = new Flashcard(flashcardId, term, definition, flashcardSetId);
                Task<Void> saveTask = db.collection("flashcards").document(flashcardId).set(flashcard);
                saveTasks.add(saveTask);

                // Update the flashcard list in the set
                db.collection("flashcardSet")
                        .document(flashcardSetId)
                        .update("flashcardList", FieldValue.arrayUnion(flashcardId));
            }
        }

        Tasks.whenAllComplete(saveTasks)
                .addOnSuccessListener(tasks -> {
                    showSaveSuccessDialog(flashcardSetId, title);
                    Toast.makeText(this, "Flashcard set saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error saving flashcards: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showSaveSuccessDialog(String flashcardSetId, String title) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_saved_successfully);

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnOpen = dialog.findViewById(R.id.btnOpen);

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        btnOpen.setOnClickListener(v -> {
            Intent intent = new Intent(this, FlashcardViewActivity.class);
            intent.putExtra("FLASHCARD_SET_ID", flashcardSetId);
            intent.putExtra("FLASHCARD_SET_TITLE", title);
            startActivity(intent);
            dialog.dismiss();
            finish();
        });

        dialog.show();
    }
}