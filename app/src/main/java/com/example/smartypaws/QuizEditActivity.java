package com.example.smartypaws;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class QuizEditActivity extends AppCompatActivity {
    private LinearLayout quizItemsContainer;
    private View lastClickedCard = null;
    private static final int MAX_OPTIONS = 4;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseAuth auth;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_AUDIO_RECORD = 3;

    private View currentQuestionView;
    private View currentOptionView;
    private String quizId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_edit);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        auth = FirebaseAuth.getInstance();

        // Retrieve the quiz ID from the Intent
        quizId = getIntent().getStringExtra("QUIZ_ID");

        // Display today's date
        TextView dateTextView = findViewById(R.id.dateText);
        String currentDate = new SimpleDateFormat("dd-MMMM-yyyy", Locale.getDefault()).format(new Date());
        dateTextView.setText(currentDate);

        quizItemsContainer = findViewById(R.id.quizItemsContainer);

        // Fetch the quiz data if editing an existing quiz
        if (quizId != null) {
            loadQuizData(quizId);
        } else {
            // Add initial quiz item
            addQuizItem();
        }

        // Setup back button
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Setup save button
        findViewById(R.id.saveButton).setOnClickListener(v -> {
            saveQuiz();
        });

        // Setup create from flashcard button
        findViewById(R.id.createFromFlashcardButton).setOnClickListener(v -> {
            // Implement create quiz from flashcard functionality
        });
    }

    private void loadQuizData(String quizId) {
        db.collection("quizzes").document(quizId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Quiz quiz = documentSnapshot.toObject(Quiz.class);

                        if (quiz != null) {
                            // Populate the UI fields with the quiz data
                            EditText titleEditText = findViewById(R.id.titleEditText);
                            EditText descriptionEditText = findViewById(R.id.descriptionEditText);
                            titleEditText.setText(quiz.getTitle());
                            descriptionEditText.setText(quiz.getDescription());

                            // Load questions and options
                            loadQuizQuestions(quiz.getQuestions());
                        }
                    } else {
                        Toast.makeText(QuizEditActivity.this, "Quiz not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(QuizEditActivity.this, "Error loading quiz data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadQuizQuestions(List<Quiz.Question> questions) {
        quizItemsContainer.removeAllViews(); // Clear existing views
        for (Quiz.Question question : questions) {
            View questionView = getLayoutInflater().inflate(R.layout.item_quiz_edit, quizItemsContainer, false);

            // Set the question text
            EditText questionEditText = questionView.findViewById(R.id.questionEditText);
            questionEditText.setText(question.getText());

            // Load options for this question
            LinearLayout optionsContainer = questionView.findViewById(R.id.optionsContainer);
            optionsContainer.removeAllViews(); // Clear default options
            for (Quiz.Question.Option option : question.getOptions()) {
                View optionView = getLayoutInflater().inflate(R.layout.item_quiz_option, optionsContainer, false);

                EditText optionEditText = optionView.findViewById(R.id.optionEditText);
                optionEditText.setText(option.getText());

                CheckBox correctCheckBox = optionView.findViewById(R.id.correctCheckBox);
                correctCheckBox.setChecked(option.isCorrect());

                setupOptionListeners(optionView, optionsContainer);
                optionsContainer.addView(optionView);
            }

            setupQuizItemListeners(questionView);
            quizItemsContainer.addView(questionView);
        }
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
            } else {
                Toast.makeText(QuizEditActivity.this, "Cannot delete the last question.", Toast.LENGTH_SHORT).show();
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
//            if (itemId == R.id.question_add_image) {
//                currentQuestionView = questionView;
//                showImagePickerDialog();
//                return true;
//            } else if (itemId == R.id.question_add_recording) {
//                currentQuestionView = questionView;
//                startAudioRecording();
//                return true;
//            } else
            if (itemId == R.id.question_add_option) {
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
//            if (itemId == R.id.option_add_image) {
//                currentOptionView = optionView;
//                showImagePickerDialog();
//                return true;
//            } else if (itemId == R.id.option_add_recording) {
//                currentOptionView = optionView;
//                startAudioRecording();
//                return true;
//            } else
            if (itemId == R.id.option_delete) {
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
        optionsContainer.addView(optionView);
        updateOptionHints(optionsContainer);
    }

    private void saveQuiz() {
        String title = ((EditText) findViewById(R.id.titleEditText)).getText().toString();
        String description = ((EditText) findViewById(R.id.descriptionEditText)).getText().toString();

        // Validate title and description
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please provide both title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        // Collect all questions
        List<Quiz.Question> questions = new ArrayList<>();

        for (int i = 0; i < quizItemsContainer.getChildCount(); i++) {
            View quizItem = quizItemsContainer.getChildAt(i);

            // Get question text
            EditText questionEditText = quizItem.findViewById(R.id.questionEditText);
            String questionText = questionEditText.getText().toString().trim();

            if (questionText.isEmpty()) {
                Toast.makeText(this, "Please provide text for all questions", Toast.LENGTH_SHORT).show();
                return;
            }

            // Collect options for the question
            LinearLayout optionsContainer = quizItem.findViewById(R.id.optionsContainer);
            List<Quiz.Question.Option> options = new ArrayList<>();

            if (!isAtLeastOneOptionChecked(optionsContainer)) {
                Toast.makeText(this, "Please mark at least one option as correct for each question", Toast.LENGTH_SHORT).show();
                return;
            }

            for (int j = 0; j < optionsContainer.getChildCount(); j++) {
                View optionView = optionsContainer.getChildAt(j);

                EditText optionEditText = optionView.findViewById(R.id.optionEditText);
                String optionText = optionEditText.getText().toString().trim();

                CheckBox correctCheckBox = optionView.findViewById(R.id.correctCheckBox);
                boolean isCorrect = correctCheckBox.isChecked();

                if (optionText.isEmpty()) {
                    Toast.makeText(this, "Please provide text for all options", Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO: Get image and audio URLs if they exist
                String imageUrl = null;
                String audioUrl = null;

                options.add(new Quiz.Question.Option(optionText, isCorrect, imageUrl, audioUrl));
            }

            // TODO: Get question image and audio URLs if they exist
            String questionImageUrl = null;
            String questionAudioUrl = null;

            Quiz.Question question = new Quiz.Question(questionText, options, questionImageUrl, questionAudioUrl);
            questions.add(question);
        }

        // Get current date in "yyyyMMdd" format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String createdAt = sdf.format(new Date());

        // Create Timestamp for lastAccessed
        Timestamp lastAccessed = Timestamp.now();

        // Get the current user's ID
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";
//        if (userId.isEmpty()) {
//            Toast.makeText(this, "Error: User not authenticated", Toast.LENGTH_SHORT).show();
//            return;
//        }

        Quiz quiz = new Quiz(userId, title, description, createdAt, lastAccessed, questions);

        // Save to Firestore
        db.collection("quizzes")
                .add(quiz)
                .addOnSuccessListener(documentReference -> {
                    String quizId = documentReference.getId();
                    quiz.setId(quizId);

                    // Update the document with the new ID
                    db.collection("quizzes").document(quizId)
                            .set(quiz)
                            .addOnSuccessListener(aVoid -> {
                                showSaveSuccessDialog(quizId, title);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error updating quiz with ID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error saving quiz: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean isAtLeastOneOptionChecked(LinearLayout optionsContainer) {
        for (int i = 0; i < optionsContainer.getChildCount(); i++) {
            View optionView = optionsContainer.getChildAt(i);
            CheckBox correctCheckBox = optionView.findViewById(R.id.correctCheckBox);
            if (correctCheckBox.isChecked()) {
                return true;
            }
        }
        return false;
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

    private void updateOptionHints(LinearLayout optionsContainer) {
        for (int i = 0; i < optionsContainer.getChildCount(); i++) {
            View optionView = optionsContainer.getChildAt(i);
            EditText optionEditText = optionView.findViewById(R.id.optionEditText);
            optionEditText.setHint("Option " + (i + 1));
        }
    }

    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Add Image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        dispatchTakePictureIntent();
                    } else {
                        dispatchPickImageIntent();
                    }
                })
                .show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void dispatchPickImageIntent() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImageIntent, REQUEST_IMAGE_PICK);
    }

    private void startAudioRecording() {
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_AUDIO_RECORD);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                case REQUEST_IMAGE_PICK:
                    handleImageResult(data);
                    break;
                case REQUEST_AUDIO_RECORD:
                    handleAudioResult(data);
                    break;
            }
        }
    }

    private void handleImageResult(Intent data) {
        Uri imageUri = (data.getData() != null) ? data.getData() : Uri.parse(data.getExtras().get("data").toString());
        uploadFileToStorage(imageUri, "images");
    }

    private void handleAudioResult(Intent data) {
        Uri audioUri = data.getData();
        uploadFileToStorage(audioUri, "audio");
    }

    private void uploadFileToStorage(Uri fileUri, String folderName) {
        String fileName = UUID.randomUUID().toString();
        StorageReference fileRef = storageRef.child(folderName + "/" + fileName);

        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        if (folderName.equals("images")) {
                            addImageToView(downloadUrl);
                        } else {
                            addAudioToView(downloadUrl);
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addImageToView(String imageUrl) {
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                300 // Set an appropriate height
        ));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

         Glide.with(this).load(imageUrl).into(imageView);

        if (currentQuestionView != null) {
            ((LinearLayout) currentQuestionView).addView(imageView, 1); // Add after question text
            currentQuestionView = null;
        } else if (currentOptionView != null) {
            ((LinearLayout) currentOptionView).addView(imageView);
            currentOptionView = null;
        }
    }

    private void addAudioToView(String audioUrl) {
        Button playButton = new Button(this);
        playButton.setText("Play Audio");
        playButton.setOnClickListener(v -> {
            // Implement audio playback here
            Toast.makeText(this, "Playing audio: " + audioUrl, Toast.LENGTH_SHORT).show();
        });

        if (currentQuestionView != null) {
            ((LinearLayout) currentQuestionView).addView(playButton, 1); // Add after question text
            currentQuestionView = null;
        } else if (currentOptionView != null) {
            ((LinearLayout) currentOptionView).addView(playButton);
            currentOptionView = null;
        }
    }
}
