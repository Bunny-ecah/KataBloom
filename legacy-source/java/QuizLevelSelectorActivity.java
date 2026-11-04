package com.example.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class QuizLevelSelectorActivity extends AppCompatActivity {

    private Button btnLevel1, btnLevel2, btnLevel3;
    private TextView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_level_selector);

        initViews();
        setupClicks();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        btnLevel1 = findViewById(R.id.btnLevel1);
        btnLevel2 = findViewById(R.id.btnLevel2);
        btnLevel3 = findViewById(R.id.btnLevel3);

        if (btnBack == null || btnLevel1 == null || btnLevel2 == null || btnLevel3 == null) {
            Toast.makeText(this, "Some buttons not found in layout!", Toast.LENGTH_LONG).show();
        }
    }

    private void setupClicks() {
        btnBack.setOnClickListener(v -> finish());

        btnLevel1.setOnClickListener(v -> openQuiz("easy"));
        btnLevel2.setOnClickListener(v -> openQuiz("medium"));
        btnLevel3.setOnClickListener(v -> openQuiz("hard"));
    }

    private void openQuiz(String level) {
        if (!ChildSessionManager.hasChild()) {
            Toast.makeText(this, "Please select a child first.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("level", level);
        intent.putExtra("childId", ChildSessionManager.childId);
        startActivity(intent);
    }
}