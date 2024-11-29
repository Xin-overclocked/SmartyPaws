package com.example.smartypaws;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import android.app.Dialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FlashcardEditActivity extends AppCompatActivity {
    private LinearLayout flashcardsContainer;
    private View lastClickedCard = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_edit);

        flashcardsContainer = findViewById(R.id.flashcardsContainer);

        // Add initial flashcard
        addFlashcard();

        // Setup upload button
        findViewById(R.id.uploadButton).setOnClickListener(v -> {
            // Handle upload functionality
        });

        // Setup save button
        findViewById(R.id.saveButton).setOnClickListener(v -> { showSaveSuccessDialog(); });

        // Setup back button
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
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

    private void showSaveSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_saved_successfully);

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnOpen = dialog.findViewById(R.id.btnOpen);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnOpen.setOnClickListener(v -> {
            // Handle the "Open" action here
            // For example, you might want to open a new activity or fragment
            Intent intent = new Intent(this, FlashcardViewActivity.class);
//            intent.putExtra("FLASHCARD_SET_ID", flashcardId);
//            intent.putExtra("FLASHCARD_SET_TITLE", flashcardTitle);
            startActivity(intent);
            dialog.dismiss();
        });

        dialog.show();
    }
}