package com.example.smartypaws;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Camera;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlashcardStudyActivity extends AppCompatActivity {
    private TextView termText;
    private TextView definitionText;
    private View flipContainer;
    private TextView progressText;
    private ProgressBar progressBar;
    private int currentPosition; // 5th card (0-based index)
    private int totalCards;
    private boolean isShowingTerm = true;
    private String flashcardSetId;
    private String flashcardSetTitle;
    private HashMap<String, String> flashcardMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_study);

        // Get flashcard id and title from the intent
        flashcardSetId = getIntent().getStringExtra("FLASHCARD_SET_ID");
        flashcardSetTitle = getIntent().getStringExtra("FLASHCARD_SET_TITLE");
        flashcardMap = (HashMap<String, String>) getIntent().getSerializableExtra("FLASHCARD_DATA");

        TextView titleTextView = findViewById(R.id.flashcardTitle);
        titleTextView.setText(flashcardSetTitle);

        // Initialize views
        termText = findViewById(R.id.termText);
        definitionText = findViewById(R.id.definitionText);
        flipContainer = findViewById(R.id.flipContainer);
        progressText = findViewById(R.id.progressText);
        progressBar = findViewById(R.id.progressBar);

        // Setup click listeners
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.prevButton).setOnClickListener(v -> showPreviousCard());
        findViewById(R.id.nextButton).setOnClickListener(v -> showNextCard());
        flipContainer.setOnClickListener(v -> flipCard());

        // Initialize progress
        updateProgress();

        // Initialize progress and total cards
        totalCards = flashcardMap.size();
        currentPosition = 0;
        updateProgress();
        showCard(currentPosition);
    }

    private void showCard(int position) {
        // Get the current term and definition based on the current position
        List<Map.Entry<String, String>> flashcardList = new ArrayList<>(flashcardMap.entrySet());
        Map.Entry<String, String> currentCard = flashcardList.get(position);

        // Update term and definition text
        String term = currentCard.getKey();
        String definition = currentCard.getValue();

        termText.setText(term);
        definitionText.setText(definition);

        // Ensure the term is visible, and the definition is hidden initially
        if (!isShowingTerm) {
            termText.setVisibility(View.VISIBLE);
            definitionText.setVisibility(View.GONE);
            flipContainer.setRotationY(0f);
            isShowingTerm = true;
        }
    }

    private void flipCard() {
        // Create flip animations
        float scale = getResources().getDisplayMetrics().density * 8000;
        Camera camera = new Camera();

        AnimatorSet flipAnimation = new AnimatorSet();

        ObjectAnimator firstHalf = ObjectAnimator.ofFloat(flipContainer, "rotationY", 0f, 90f);
        firstHalf.setDuration(150);
        firstHalf.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator secondHalf = ObjectAnimator.ofFloat(flipContainer, "rotationY", -90f, 0f);
        secondHalf.setDuration(150);
        secondHalf.setInterpolator(new DecelerateInterpolator());

        firstHalf.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Switch visibility at the middle of the animation
                if (isShowingTerm) {
                    termText.setVisibility(View.GONE);
                    definitionText.setVisibility(View.VISIBLE);
                } else {
                    termText.setVisibility(View.VISIBLE);
                    definitionText.setVisibility(View.GONE);
                }
                isShowingTerm = !isShowingTerm;
            }
        });

        flipAnimation.playSequentially(firstHalf, secondHalf);
        flipContainer.setCameraDistance(scale);
        flipAnimation.start();
    }

    private void showPreviousCard() {
        if (currentPosition > 0) {
            currentPosition--;
            updateProgress();
            showCard(currentPosition);
        }
    }

    private void showNextCard() {
        if (currentPosition < totalCards - 1) {
            currentPosition++;
            updateProgress();
            showCard(currentPosition);
        }
    }

    private void updateProgress() {
        progressText.setText(String.format("%d / %d", currentPosition + 1, totalCards));
        int progress = (int) (((float) (currentPosition + 1) / totalCards) * 100);
        progressBar.setProgress(progress);
    }
}