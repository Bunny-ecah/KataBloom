package com.example.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LearningActivity extends BaseActivity {

    private LinearLayout cardPhonics, cardSpeech, cardQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        cardPhonics = findViewById(R.id.cardPhonics);
        cardSpeech = findViewById(R.id.cardSpeech);
        cardQuiz = findViewById(R.id.cardQuiz);

        findViewById(R.id.btnAdventure).setOnClickListener(v -> startActivity(new Intent(this, AdventureActivity.class)));
        setupClicks();
    }

    private void setupClicks() {
        cardPhonics.setOnClickListener(v -> {
            if (!ChildSessionManager.hasChild()) {
                Toast.makeText(this, "Please select a child first", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(this, PhonicsLevelSelectorActivity.class);
            startActivity(i);
        });

        cardSpeech.setOnClickListener(v -> {
            if (!ChildSessionManager.hasChild()) {
                Toast.makeText(this, "Please select a child first", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(this, SpeechLevelSelectorActivity.class);
            startActivity(i);
        });

        cardQuiz.setOnClickListener(v -> {
            if (!ChildSessionManager.hasChild()) {
                Toast.makeText(this, "Please select a child first", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(this, QuizLevelSelectorActivity.class);
            startActivity(i);
        });
    }
}