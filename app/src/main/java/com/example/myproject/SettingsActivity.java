package com.example.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends BaseActivity {

    // ---------------- UI ----------------
    TextView tvParentName, tvParentEmail;
    TextView tvActiveChildName, tvActiveChildAge;
    Button btnEditParent, btnChangeChild, btnAddChild, btnEditProfile, btnDeleteChild, btnLogout;

    RecyclerView rvChildren;

    // ---------------- FIREBASE ----------------
    FirebaseAuth auth;
    FirebaseFirestore db;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            ChildSessionManager.clear();
            Intent i = new Intent(SettingsActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
        userId = auth.getCurrentUser().getUid();

        initViews();
        loadParentProfile();
        loadChildren();
        setupButtons();
    }

    private void initViews() {
        // Parent info
        tvParentName = findViewById(R.id.tvParentName);
        tvParentEmail = findViewById(R.id.tvParentEmail);
        btnEditParent = findViewById(R.id.btnEditParent);

        // Active child
        tvActiveChildName = findViewById(R.id.tvActiveChildName);
        tvActiveChildAge = findViewById(R.id.tvActiveChildAge);
        btnChangeChild = findViewById(R.id.btnChangeChild);

        // Child list
        rvChildren = findViewById(R.id.rvChildren);

        // Child buttons
        btnAddChild = findViewById(R.id.btnAddChild);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnDeleteChild = findViewById(R.id.btnDeleteChild);
    }

    private void loadParentProfile() {
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        tvParentName.setText(doc.getString("name"));
                        tvParentEmail.setText(doc.getString("email"));
                    }
                });
    }

    private void loadChildren() {
        db.collection("users")
                .document(userId)
                .collection("children")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Child> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot) {
                        Child c = new Child();
                        c.id = doc.getId();
                        c.name = doc.getString("name");
                        c.age = doc.getString("age");
                        list.add(c);
                    }
                    setupRecycler(list);
                });
    }

    private void setupRecycler(List<Child> list) {
        ChildAdapter adapter = new ChildAdapter(this, list, child -> {
            ChildSessionManager.setChild(userId, child.id, child.name, child.age);
            tvActiveChildName.setText(child.name);
            tvActiveChildAge.setText("Age: " + child.age);
            Toast.makeText(this, "Child selected", Toast.LENGTH_SHORT).show();
        });
        rvChildren.setLayoutManager(new LinearLayoutManager(this));
        rvChildren.setAdapter(adapter);
    }

    private void setupButtons() {
        btnAddChild.setOnClickListener(v -> startActivity(new Intent(this, AddChildActivity.class)));

        btnEditProfile.setOnClickListener(v -> {
            if (!ChildSessionManager.hasChild()) {
                Toast.makeText(this, "No child selected", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(this, ChildProfileActivity.class);
            i.putExtra("childId", ChildSessionManager.childId);
            startActivity(i);
        });

        btnDeleteChild.setOnClickListener(v -> confirmDeleteChild());

        btnChangeChild.setOnClickListener(v -> startActivity(new Intent(this, AddChildActivity.class)));

        btnEditParent.setOnClickListener(v -> startActivity(new Intent(this, EditProfileActivity.class)));
    }

    private void confirmDeleteChild() {
        if (!ChildSessionManager.hasChild()) {
            Toast.makeText(this, "No child selected", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Delete Child")
                .setMessage("Are you sure?")
                .setPositiveButton("Delete", (d, w) -> deleteChild())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteChild() {
        db.collection("users")
                .document(userId)
                .collection("children")
                .document(ChildSessionManager.childId)
                .delete()
                .addOnSuccessListener(unused -> {
                    ChildSessionManager.clear();
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                    loadChildren();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadParentProfile();
        loadChildren();
    }
}