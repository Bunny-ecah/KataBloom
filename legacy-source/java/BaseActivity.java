package com.example.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class BaseActivity extends AppCompatActivity {

    protected FirebaseAuth auth;
    protected String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            redirectToLogin();
            return;
        }

        userId = auth.getCurrentUser().getUid();

        // only call after layout is ready
        getWindow().getDecorView().post(this::setupBottomNav);
    }

    protected void redirectToLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void setupBottomNav() {

        View navHome = findViewById(R.id.navHome);
        View navLearn = findViewById(R.id.navLearn);
        View navReport = findViewById(R.id.navReport);
        View navSettings = findViewById(R.id.navSettings);

        if (navHome != null) {
            navHome.setOnClickListener(v ->
                    startActivity(new Intent(this, DashboardActivity.class)));
        }

        if (navLearn != null) {
            navLearn.setOnClickListener(v ->
                    startActivity(new Intent(this, LearningActivity.class)));
        }

        if (navReport != null) {
            navReport.setOnClickListener(v ->
                    startActivity(new Intent(this, ProgressActivity.class)));
        }

        if (navSettings != null) {
            navSettings.setOnClickListener(v ->
                    startActivity(new Intent(this, SettingsActivity.class)));
        }
    }
}