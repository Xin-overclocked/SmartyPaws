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

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_edit);

        // Firebase configuration
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Retrieve the quiz ID from the Intent
        flashcardSetId = getIntent().getStringExtra("FLASHCARDSET_ID");

        flashcardsContainer = findViewById(R.id.flashcardsContainer);
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEdit);

        // Fetch the quiz data if editing an existing quiz
        if (flashcardSetId != null) {
            loadFlashCardSetData(flashcardSetId);
        } else {
            // Add initial quiz item
            addFlashcard();
        }

        // Setup upload button
        findViewById(R.id.uploadButton).setOnClickListener(v -> {
            // Handle upload functionality
        });

        // Get the current user ID
        currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        // Setup save button
        findViewById(R.id.saveButton).setOnClickListener(v -> saveFlashcardSet());

        // Setup back button
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
                    } else {
                        Toast.makeText(this, "FlashcardSet not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading FlashcardSet: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadFlashCards(String flashcardSetId) {
        db.collection("flashcards")
                .whereEqualTo("flashcardSetId", flashcardSetId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    flashcardsContainer.removeAllViews();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Flashcard flashcard = document.toObject(Flashcard.class);
                        addFlashcardToUI(flashcard);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading flashcards: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void addFlashcard() {
        View cardView = getLayoutInflater().inflate(R.layout.item_flashcard_edit, flashcardsContainer, false);
        setupCardListeners(cardView);
        flashcardsContainer.addView(cardView);
    }

    private void setupCardListeners(View cardView) {
        CardView card = cardView.findViewById(R.id.cardView);
        LinearLayout buttonContainer = cardView.findViewById(R.id.buttonContainer);
        FloatingActionButton addButton = cardView.findViewById(R.id.addButton);
        FloatingActionButton deleteButton = cardView.findViewById(R.id.deleteButton);

        // Card click listener
        card.setOnClickListener(v -> {
            // Hide previous buttons if different card is clicked
            if (lastClickedCard != null && lastClickedCard != cardView) {
                lastClickedCard.findViewById(R.id.buttonContainer).setVisibility(View.GONE);
            }

            // Toggle buttons visibility
            boolean isVisible = buttonContainer.getVisibility() == View.VISIBLE;
            buttonContainer.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            lastClickedCard = cardView;
        });

        // Add button listener
        addButton.setOnClickListener(v -> {
            addFlashcard();
            buttonContainer.setVisibility(View.GONE);
        });

        // Delete button listener
        deleteButton.setOnClickListener(v -> {
            if (flashcardsContainer.getChildCount() > 1) {
                flashcardsContainer.removeView(cardView);
            }
        });

        // Menu button listener
        cardView.findViewById(R.id.termMenuButton).setOnClickListener(v -> {
            // Show menu options
            PopupMenu popup = new PopupMenu(this, v);
            popup.inflate(R.menu.flashcard_edit_menu);
            popup.show();
        });

        cardView.findViewById(R.id.definitionMenuButton).setOnClickListener(v -> {
            // Show menu options
            PopupMenu popup = new PopupMenu(this, v);
            popup.inflate(R.menu.flashcard_edit_menu);
            popup.show();
        });
    }

    private void addFlashcardToUI(Flashcard flashcard) {
        View flashcardView = getLayoutInflater().inflate(R.layout.item_flashcard_edit, flashcardsContainer, false);

        EditText termEditText = flashcardView.findViewById(R.id.termEdit);
        EditText definitionEditText = flashcardView.findViewById(R.id.definitionEdit);

        termEditText.setText(flashcard.getTerm());
        definitionEditText.setText(flashcard.getDefinition());

        flashcardsContainer.addView(flashcardView);
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

        flashcardSetId = setRef.getId(); // Ensure ID is available for child flashcards
        flashcardSet.setId(flashcardSetId);

        // Use batch write for better performance
        WriteBatch batch = db.batch();
        batch.set(setRef, flashcardSet); // Add or update FlashcardSet

        batch.commit()
                .addOnSuccessListener(aVoid -> saveFlashcard(flashcardSetId, title))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error saving FlashcardSet: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void saveFlashcard(String flashcardSetId, String title) {
        List<Task<Void>> saveTasks = new ArrayList<>();

        for (int i = 0; i < flashcardsContainer.getChildCount(); i++) {
            View flashcardView = flashcardsContainer.getChildAt(i);
            EditText termEditText = flashcardView.findViewById(R.id.termEdit);
            EditText definitionEditText = flashcardView.findViewById(R.id.definitionEdit);

            String term = termEditText.getText().toString().trim();
            String definition = definitionEditText.getText().toString().trim();

            if (!term.isEmpty() && !definition.isEmpty()) {
                String flashcardId = db.collection("flashcards").document().getId(); // New ID

                // Create or update the flashcard
                Flashcard flashcard = new Flashcard(flashcardId, term, definition, flashcardSetId);
                Task<Void> saveTask = db.collection("flashcards").document(flashcardId).set(flashcard);
                saveTasks.add(saveTask);

                // Add ID to flashcardSet
                db.collection("flashcardSet")
                        .document(flashcardSetId)
                        .update("flashcardList", FieldValue.arrayUnion(flashcardId));
            }
        }

        // Batch save flashcards
        Tasks.whenAllComplete(saveTasks)
                .addOnSuccessListener(completedTasks -> {
                    showSaveSuccessDialog(flashcardSetId, title);
                    Toast.makeText(FlashcardEditActivity.this,
                            "Flashcard set saved successfully",
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(FlashcardEditActivity.this,
                            "Error saving flashcards: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void showSaveSuccessDialog(String quizId, String quizTitle) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_saved_successfully);

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnOpen = dialog.findViewById(R.id.btnOpen);

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });
        btnOpen.setOnClickListener(v -> {
            Intent intent = new Intent(this, QuizViewActivity.class);
            intent.putExtra("QUIZ_ID", quizId);
            intent.putExtra("QUIZ_TITLE", quizTitle);
            startActivity(intent);
            dialog.dismiss();
            finish();
        });

        dialog.show();
    }
}

//    @SuppressLint("SimpleDateFormat")
//    private void saveFlashcardSet() {
//        String title = titleEditText.getText().toString().trim();
//        String description = descriptionEditText.getText().toString().trim();
//
//        if (title.isEmpty() || description.isEmpty()) {
//            Toast.makeText(this, "Title and description cannot be empty.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        FlashcardSet flashcardSet = new FlashcardSet();
//        flashcardSet.setTitle(title);
//        flashcardSet.setDescription(description);
//        flashcardSet.setUserid(currentUserId);
//        flashcardSet.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
//        flashcardSet.setLastAccessed(new Timestamp(new Date()));
//
//        if (flashcardSetId == null) {
//            // New FlashcardSet
//            db.collection("flashcardSet")
//                    .add(flashcardSet)
//                    .addOnSuccessListener(documentReference -> {
//                        flashcardSetId = documentReference.getId();
//                        flashcardSet.setId(flashcardSetId);
//                        documentReference.set(flashcardSet)
//                                .addOnSuccessListener(aVoid -> {
//                                    saveFlashcards(flashcardSetId, title);
//                                })
//                                .addOnFailureListener(e -> {
//                                    Toast.makeText(this, "Error updating FlashcardSet ID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                });
//                    })
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(this, "Error saving FlashcardSet: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    });
//        } else {
//            // Update existing FlashcardSet
//            db.collection("flashcardSet").document(flashcardSetId)
//                    .set(flashcardSet)
//                    .addOnSuccessListener(aVoid -> saveFlashcards(flashcardSetId, title))
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(this, "Error updating FlashcardSet: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    });
//        }
//    }
//
//    private void saveFlashcards(String flashcardSetId, String title) {
//        db.collection("flashcards")
//                .whereEqualTo("flashcardSetId", flashcardSetId)
//                .get()
//                .addOnSuccessListener(querySnapshot -> {
//                    // Delete existing flashcards
//                    List<Task<Void>> deleteTasks = new ArrayList<>();
//                    for (QueryDocumentSnapshot document : querySnapshot) {
//                        deleteTasks.add(document.getReference().delete());
//                    }
//
//                    // Wait for all deletions to complete
//                    Tasks.whenAllComplete(deleteTasks)
//                            .addOnSuccessListener(tasks -> {
//                                // Save new flashcards
//                                List<Task<Void>> saveTasks = new ArrayList<>();
//
//                                for (int i = 0; i < flashcardsContainer.getChildCount(); i++) {
//                                    View flashcardView = flashcardsContainer.getChildAt(i);
//                                    EditText termEditText = flashcardView.findViewById(R.id.termEdit);
//                                    EditText definitionEditText = flashcardView.findViewById(R.id.definitionEdit);
//
//                                    String term = termEditText.getText().toString().trim();
//                                    String definition = definitionEditText.getText().toString().trim();
//
//                                    if (!term.isEmpty() && !definition.isEmpty()) {
//                                        // Generate a unique ID for the flashcard
//                                        String flashcardId = db.collection("flashcards").document().getId();
//
//                                        // Create a flashcard object with the generated ID
//                                        Flashcard flashcard = new Flashcard(flashcardId,term, definition, flashcardSetId);
//
//                                        // Save the flashcard with its ID
//                                        Task<Void> saveTask = db.collection("flashcards")
//                                                .document(flashcardId)
//                                                .set(flashcard);
//                                        saveTasks.add(saveTask);
//                                    }
//                                }
//
//                                // Wait for all saves to complete
//                                Tasks.whenAllComplete(saveTasks)
//                                        .addOnSuccessListener(completedTasks -> {
//                                            showSaveSuccessDialog(flashcardSetId, title);
//                                            Toast.makeText(FlashcardEditActivity.this,
//                                                    "Flashcard set saved successfully",
//                                                    Toast.LENGTH_SHORT).show();
//                                            finish();
//                                        })
//                                        .addOnFailureListener(e -> {
//                                            Toast.makeText(FlashcardEditActivity.this,
//                                                    "Error saving flashcards: " + e.getMessage(),
//                                                    Toast.LENGTH_SHORT).show();
//                                        });
//                            });
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Error deleting old flashcards: " + e.getMessage(),
//                            Toast.LENGTH_SHORT).show();
//                });
//    }