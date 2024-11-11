package com.example.smartypaws;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
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

        // Set up the search functionality
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search submit
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle search text changes
                return true;
            }
        });

        // Set up RecyclerViews
        recentlyStudiedRecyclerView = findViewById(R.id.recentlyStudiedRecyclerView);
        myFlashcardsRecyclerView = findViewById(R.id.myFlashcardsRecyclerView);

        recentlyStudiedRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        myFlashcardsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Sample data - replace with your actual data
        List<Flashcard> recentlyStudiedList = new ArrayList<>();
        List<Flashcard> myFlashcardsList = new ArrayList<>();

        // Add sample flashcards
        recentlyStudiedList.add(new Flashcard("Recently Studied 1", "Description 1"));
        recentlyStudiedList.add(new Flashcard("Recently Studied 2", "Description 2"));
        myFlashcardsList.add(new Flashcard("My Flashcard 1", "Description 1"));
        myFlashcardsList.add(new Flashcard("My Flashcard 2", "Description 2"));

        recentlyStudiedAdapter = new FlashcardAdapter(recentlyStudiedList);
        myFlashcardsAdapter = new FlashcardAdapter(myFlashcardsList);

        recentlyStudiedRecyclerView.setAdapter(recentlyStudiedAdapter);
        myFlashcardsRecyclerView.setAdapter(myFlashcardsAdapter);

        // Set up FAB click listener
        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle FAB click (e.g., open a dialog to add a new flashcard)
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
}