package com.example.smartypaws;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartypaws.databinding.ActivityMainBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private BarChart barChart;
    private FloatingActionButton fab;

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        barChart = findViewById(R.id.bar_chart);
        fab = findViewById(R.id.fabAdd);

        setupBarChart();
        setupFAB();

        // Set up bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                    overrideActivityTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
            }
            if (item.getItemId() == R.id.nav_profile) {
                    return true;
            }
            return false;
        });

    }

    private void setupBarChart() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 4f));
        entries.add(new BarEntry(1f, 3f));
        entries.add(new BarEntry(2f, 5f));
        entries.add(new BarEntry(3f, 2f));
        entries.add(new BarEntry(4f, 4f));
        entries.add(new BarEntry(5f, 3f));
        entries.add(new BarEntry(6f, 6f));

        BarDataSet set = new BarDataSet(entries, "Screen Time");
        set.setColor(Color.parseColor("#553479"));

        BarData data = new BarData(set);
        data.setBarWidth(0.5f);

        barChart.setData(data);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.getAxisLeft().setTextColor(Color.BLACK);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setTextColor(Color.BLACK);

        String[] days = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(days));

        barChart.invalidate();
    }

    private void setupFAB() {
        // Set up FAB click listener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a dialog instance
                Dialog dialog = new Dialog(ProfileActivity.this);
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
    }

}