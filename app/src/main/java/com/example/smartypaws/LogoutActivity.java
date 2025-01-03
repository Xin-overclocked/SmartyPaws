package com.example.smartypaws;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutActivity extends AppCompatActivity {

    private MaterialButton logoutButton, cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logout_activity);

        logoutButton = findViewById(R.id.logoutButton);
        cancelButton = findViewById(R.id.cancelButton);

        logoutButton.setOnClickListener(v -> showLogoutDialog());
        cancelButton.setOnClickListener(v -> finish());

    }
    private void showLogoutDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog,which) -> performLogout())
                .setNegativeButton("No", null)
                .show();
    }

    private void performLogout(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut(); //Sign Out the user
        Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);//add animation
        finish();//close logoutActivity
    }
}
