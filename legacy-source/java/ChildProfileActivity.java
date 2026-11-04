package com.example.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ChildProfileActivity extends BaseActivity {

    EditText etChildName, etChildAge;
    Button btnStartLearning, btnUpdateChild, btnDeleteChild;
    TextView btnBack;

    FirebaseAuth auth;
    FirebaseFirestore db;

    String parentId;
    String childId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_profile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        parentId = currentUser.getUid();
        childId = getIntent().getStringExtra("childId");

        initViews();

        if (!TextUtils.isEmpty(childId)) {
            loadChildData();
        }

        btnBack.setOnClickListener(v -> finish());

        btnStartLearning.setOnClickListener(v -> saveChildAndStartLearning());

        btnUpdateChild.setOnClickListener(v -> saveChildProfile(false));

        btnDeleteChild.setOnClickListener(v -> confirmDelete());
    }

    private void initViews() {
        etChildName = findViewById(R.id.etChildName);
        etChildAge = findViewById(R.id.etChildAge);

        btnStartLearning = findViewById(R.id.btnStartLearning);
        btnUpdateChild = findViewById(R.id.btnUpdateChild);
        btnDeleteChild = findViewById(R.id.btnDeleteChild);

        btnBack = findViewById(R.id.btnBack);
    }

    private void loadChildData() {
        db.collection("users")
                .document(parentId)
                .collection("children")
                .document(childId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        etChildName.setText(documentSnapshot.getString("name"));
                        etChildAge.setText(documentSnapshot.getString("age"));
                    } else {
                        Toast.makeText(this, "Child profile not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading child data: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void saveChildAndStartLearning() {
        saveChildProfile(true);
    }

    private void saveChildProfile(boolean startLearningAfterSave) {
        String name = etChildName.getText().toString().trim();
        String age = etChildAge.getText().toString().trim();

        if (name.isEmpty() || age.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> childData = new HashMap<>();
        childData.put("name", name);
        childData.put("age", age);
        childData.put("updatedAt", FieldValue.serverTimestamp());

        DocumentReference childRef;

        if (TextUtils.isEmpty(childId)) {
            childRef = db.collection("users")
                    .document(parentId)
                    .collection("children")
                    .document();

            childId = childRef.getId();
            childData.put("createdAt", FieldValue.serverTimestamp());
        } else {
            childRef = db.collection("users")
                    .document(parentId)
                    .collection("children")
                    .document(childId);
        }

        childRef.set(childData)
                .addOnSuccessListener(unused -> {
                    ChildSessionManager.setChild(parentId, childId, name, age);

                    if (startLearningAfterSave) {
                        Intent intent = new Intent(this, LearningActivity.class);
                        intent.putExtra("childId", childId);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Child profile saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Save failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void confirmDelete() {
        if (TextUtils.isEmpty(childId)) {
            Toast.makeText(this, "No child profile to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete Child Profile")
                .setMessage("Are you sure you want to delete this child profile?")
                .setPositiveButton("Delete", (dialog, which) -> deleteChild())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteChild() {
        db.collection("users")
                .document(parentId)
                .collection("children")
                .document(childId)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Child profile deleted", Toast.LENGTH_SHORT).show();

                    ChildSessionManager.clear();

                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Delete failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}