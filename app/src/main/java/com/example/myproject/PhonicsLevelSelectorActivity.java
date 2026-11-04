package com.example.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PhonicsLevelSelectorActivity extends AppCompatActivity {

    Button btnLevel1, btnLevel2, btnLevel3;
    TextView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonics_level_selector);

        btnBack = findViewById(R.id.btnBack);

        btnLevel1 = findViewById(R.id.btnLevel1);
        btnLevel2 = findViewById(R.id.btnLevel2);
        btnLevel3 = findViewById(R.id.btnLevel3);

        btnBack.setOnClickListener(v -> finish());

        btnLevel1.setOnClickListener(v -> openPhonics("easy"));
        btnLevel2.setOnClickListener(v -> openPhonics("medium"));
        btnLevel3.setOnClickListener(v -> openPhonics("hard"));
    }

    private void openPhonics(String level) {
        if (!ChildSessionManager.hasChild()) {
            Toast.makeText(this, "Please select a child first", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, PhonicsActivity.class);
        intent.putExtra("level", level);
        intent.putExtra("childId", ChildSessionManager.childId);
        startActivity(intent);
    }
}