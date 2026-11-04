package com.example.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddChildActivity extends BaseActivity {

    EditText etChildName, etChildAge;
    Button btnSaveChild, btnBack;

    FirebaseFirestore db;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        // ------------------- INIT -------------------
        etChildName = findViewById(R.id.etChildName);
        etChildAge = findViewById(R.id.etChildAge);
        btnSaveChild = findViewById(R.id.btnSaveChild);
        btnBack = findViewById(R.id.btnBack);

        // ------------------- FIREBASE -------------------
        db = FirebaseFirestore.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Session expired. Please login again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // ------------------- LISTENERS -------------------
        btnSaveChild.setOnClickListener(v -> saveChild());

        btnBack.setOnClickListener(v -> finish());
    }

    private void saveChild() {

        String name = etChildName.getText().toString().trim();
        String age = etChildAge.getText().toString().trim();

        if (name.isEmpty() || age.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a new child document ID
        String childId = db.collection("users")
                .document(userId)
                .collection("children")
                .document()
                .getId();

        Map<String, Object> child = new HashMap<>();
        child.put("id", childId);
        child.put("name", name);
        child.put("age", age);

        db.collection("users")
                .document(userId)
                .collection("children")
                .document(childId)
                .set(child)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Child profile added", Toast.LENGTH_SHORT).show();

                    // Set the session so this child becomes active
                    ChildSessionManager.setChild(userId, childId, name, age);

                    Intent intent = new Intent(AddChildActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}