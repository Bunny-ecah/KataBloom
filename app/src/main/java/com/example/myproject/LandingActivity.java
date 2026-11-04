package com.example.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LandingActivity extends AppCompatActivity {

    Button btnGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        findViewById(android.R.id.content).setOnApplyWindowInsetsListener((v,insets) -> {
            v.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(), insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            return insets;
        });

        btnGetStarted = findViewById(R.id.btnGetStarted);
        findViewById(R.id.btnAdventure).setOnClickListener(v -> startActivity(new Intent(this, AdventureActivity.class)));

        btnGetStarted.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });
    }
}
