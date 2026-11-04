package com.example.myproject.core;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProgressTracker {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void update(String userId, String childId,
                       String skill, boolean correct) {

        String path = "users/" + userId + "/children/" + childId + "/progress/" + skill;

        Map<String, Object> data = new HashMap<>();
        data.put(correct ? "correct" : "wrong", 1);

        db.document(path)
                .update(data)
                .addOnFailureListener(e ->
                        db.document(path).set(data));
    }
}