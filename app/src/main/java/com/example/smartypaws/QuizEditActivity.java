package com.example.smartypaws;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class QuizEditActivity extends AppCompatActivity {
    private LinearLayout quizItemsContainer;
    private View lastClickedCard = null;
    private static final int MAX_OPTIONS = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_edit);

        quizItemsContainer = findViewById(R.id.quizItemsContainer);

        // Add initial quiz item
        addQuizItem();

        // Setup back button
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Setup save button
        findViewById(R.id.saveButton).setOnClickListener(v -> {
            saveQuiz();
            showSaveSuccessDialog();
        });

        // Setup create from flashcard button
        findViewById(R.id.createFromFlashcardButton).setOnClickListener(v -> {
            // Implement create quiz from flashcard functionality
        });
    }

    private void addQuizItem() {
        View itemView = getLayoutInflater().inflate(R.layout.item_quiz_edit, quizItemsContainer, false);
        setupQuizItemListeners(itemView);
        quizItemsContainer.addView(itemView);
    }

    private void setupQuizItemListeners(View itemView) {
        CardView card = itemView.findViewById(R.id.cardView);
        ImageButton questionMenuButton = itemView.findViewById(R.id.questionMenuButton);
        LinearLayout buttonContainer = itemView.findViewById(R.id.buttonContainer);
        FloatingActionButton addButton = itemView.findViewById(R.id.addButton);
        FloatingActionButton deleteButton = itemView.findViewById(R.id.deleteButton);

        // Card click listener
        card.setOnClickListener(v -> {
            // Hide previous buttons if different card is clicked
            if (lastClickedCard != null && lastClickedCard != itemView) {
                lastClickedCard.findViewById(R.id.buttonContainer).setVisibility(View.GONE);
            }

            // Toggle buttons visibility
            boolean isVisible = buttonContainer.getVisibility() == View.VISIBLE;
            buttonContainer.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            lastClickedCard = itemView;
        });

        // Question menu button listener
        questionMenuButton.setOnClickListener(v -> showQuestionMenu(v));

        // Setup option menu buttons
        LinearLayout optionsContainer = itemView.findViewById(R.id.optionsContainer);
        for (int i = 0; i < optionsContainer.getChildCount(); i++) {
            View optionView = optionsContainer.getChildAt(i);
            setupOptionListeners(optionView, optionsContainer);
        }

        // Add button listener
        addButton.setOnClickListener(v -> addQuizItem());

        // Delete button listener
        deleteButton.setOnClickListener(v -> {
            if (quizItemsContainer.getChildCount() > 1) {
                quizItemsContainer.removeView(itemView);
            }
        });
    }

    private void setupOptionListeners(View optionView, LinearLayout optionsContainer) {
        ImageButton optionMenuButton = optionView.findViewById(R.id.optionMenuButton);
        CheckBox correctCheckBox = optionView.findViewById(R.id.correctCheckBox);

        optionMenuButton.setOnClickListener(v -> showOptionMenu(v, optionView, optionsContainer));

        correctCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                for (int i = 0; i < optionsContainer.getChildCount(); i++) {
                    View otherOptionView = optionsContainer.getChildAt(i);
                    if (otherOptionView != optionView) {
                        CheckBox otherCheckBox = otherOptionView.findViewById(R.id.correctCheckBox);
                        otherCheckBox.setChecked(false);
                    }
                }
            }
        });
    }

    private void showQuestionMenu(View anchorView) {
        PopupMenu popup = new PopupMenu(this, anchorView);
        popup.getMenuInflater().inflate(R.menu.quiz_question_menu, popup.getMenu());

        // Disable "Add Option" if max options reached
        View questionView = (View) anchorView.getParent().getParent();
        LinearLayout optionsContainer = questionView.findViewById(R.id.optionsContainer);
        popup.getMenu().findItem(R.id.question_add_option).setEnabled(
                optionsContainer.getChildCount() < MAX_OPTIONS
        );

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.question_add_image) {
                // Implement image adding
                return true;
            } else if (itemId == R.id.question_add_recording) {
                // Implement recording adding
                return true;
            } else if (itemId == R.id.question_add_option) {
                addNewOption(optionsContainer);
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void showOptionMenu(View anchorView, View optionView, LinearLayout optionsContainer) {
        PopupMenu popup = new PopupMenu(this, anchorView);
        popup.getMenuInflater().inflate(R.menu.quiz_option_menu, popup.getMenu());

        // Disable the delete option if there are only 2 options
        popup.getMenu().findItem(R.id.option_delete).setEnabled(optionsContainer.getChildCount() > 2);

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.option_add_image) {
                // Implement image adding
                return true;
            } else if (itemId == R.id.option_add_recording) {
                // Implement recording adding
                return true;
            } else if (itemId == R.id.option_delete) {
                if (optionsContainer.getChildCount() > 2) {
                    optionsContainer.removeView(optionView);
                    updateOptionHints(optionsContainer);
                } else {
                    Toast.makeText(QuizEditActivity.this, "Cannot delete. Minimum two options required.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void addNewOption(LinearLayout optionsContainer) {
        if (optionsContainer.getChildCount() >= MAX_OPTIONS) {
            Toast.makeText(this, "Maximum number of options reached", Toast.LENGTH_SHORT).show();
            return;
        }

        View optionView = getLayoutInflater().inflate(R.layout.item_quiz_option, optionsContainer, false);

        EditText optionEditText = optionView.findViewById(R.id.optionEditText);
        optionEditText.setHint("Option " + (optionsContainer.getChildCount() + 1));

        setupOptionListeners(optionView, optionsContainer);
        updateOptionHints(optionsContainer);
        optionsContainer.addView(optionView);
    }

    private void saveQuiz() {
        String title = ((EditText) findViewById(R.id.titleEditText)).getText().toString();
        String description = ((EditText) findViewById(R.id.descriptionEditText)).getText().toString();

        // TODO: Implement quiz saving logic
        Toast.makeText(this, "Quiz saved successfully", Toast.LENGTH_SHORT).show();
    }

    private void showSaveSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_saved_successfully);

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnOpen = dialog.findViewById(R.id.btnOpen);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnOpen.setOnClickListener(v -> {
            Intent intent = new Intent(this, QuizViewActivity.class);
//            intent.putExtra("QUIZ_ID", quizId);
//            intent.putExtra("QUIZ_TITLE", quizTitle);
            startActivity(intent);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateOptionHints(LinearLayout optionsContainer) {
        for (int i = 0; i < optionsContainer.getChildCount(); i++) {
            View optionView = optionsContainer.getChildAt(i);
            EditText optionEditText = optionView.findViewById(R.id.optionEditText);
            optionEditText.setHint("Option " + (i + 1));
        }
    }
}
