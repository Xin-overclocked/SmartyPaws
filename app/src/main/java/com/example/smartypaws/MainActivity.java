package com.example.smartypaws;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recentlyStudiedRecyclerView;
    private RecyclerView myFlashcardsRecyclerView;
    private FlashcardAdapter recentlyStudiedAdapter;
    private FlashcardAdapter myFlashcardsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up RecyclerViews
        recentlyStudiedRecyclerView = findViewById(R.id.recentlyStudiedRecyclerView);
        myFlashcardsRecyclerView = findViewById(R.id.myFlashcardsRecyclerView);

        recentlyStudiedRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        myFlashcardsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Sample data - replace with your actual data
        List<FlashcardSet> recentlyStudiedList = new ArrayList<>();
        List<FlashcardSet> myFlashcardsList = new ArrayList<>();

        // Add sample flashcards
        recentlyStudiedList.add(new FlashcardSet("Recently Studied 1", "Description 1"));
        recentlyStudiedList.add(new FlashcardSet("Recently Studied 2", "Description 2"));
        myFlashcardsList.add(new FlashcardSet("My Flashcard 1", "Description 1"));
        myFlashcardsList.add(new FlashcardSet("My Flashcard 2", "Description 2"));

        // Create adapters with click listeners
        recentlyStudiedAdapter = new FlashcardAdapter(recentlyStudiedList, flashcardSet -> navigateToFlashcardView(flashcardSet));
        myFlashcardsAdapter = new FlashcardAdapter(myFlashcardsList, flashcardSet -> navigateToFlashcardView(flashcardSet));

        recentlyStudiedRecyclerView.setAdapter(recentlyStudiedAdapter);
        myFlashcardsRecyclerView.setAdapter(myFlashcardsAdapter);

        // Set up FAB click listener
        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a dialog instance
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.plus_btn_options);

                // Reference buttons inside the dialog
                Button createFlashcard = dialog.findViewById(R.id.btn_create_flashcard);
                Button createQuiz = dialog.findViewById(R.id.btn_create_quiz);

                // Set click listeners for the dialog buttons
                createFlashcard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Action for creating a new flashcard
                        startActivity(new Intent(getApplicationContext(), FlashcardEditActivity.class));
                        dialog.dismiss();

                    }
                });

                createQuiz.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Action for creating a new quiz
                        dialog.dismiss();
                        // Add your navigation or creation logic here
                    }
                });

                // Show the dialog
                dialog.show();
            }
        });

        // Set up bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
//                    overrideActivityTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            if (item.getItemId() == R.id.nav_home) {
                return true;
            }
            return false;
        });
    }

    private void navigateToFlashcardView(FlashcardSet flashcardSet) {
        Intent intent = new Intent(MainActivity.this, FlashcardViewActivity.class);
        intent.putExtra("FLASHCARD_SET_ID", flashcardSet.getId()); // Assuming Flashcard has an getId() method
        intent.putExtra("FLASHCARD_SET_TITLE", flashcardSet.getTitle());
        intent.putExtra("FLASHCARD_SET_DESCRIPTION", flashcardSet.getDescription());
        startActivity(intent);
    }
}