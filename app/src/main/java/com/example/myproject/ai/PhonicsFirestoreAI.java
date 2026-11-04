package com.example.myproject.ai;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class PhonicsFirestoreAI {

    private FirebaseFirestore db;

    public PhonicsFirestoreAI() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Update Firestore progress for a child.
     *
     * @param userId    Parent / user ID
     * @param childId   Child ID
     * @param letter    The letter being answered
     * @param correct   True if the child got it correct, false otherwise
     */
    public void updateProgress(String userId, String childId, String letter, boolean correct) {

        String path = "users/" + userId + "/children/" + childId + "/phonics";
        Map<String, Object> update = new HashMap<>();

        if (correct) {
            update.put(letter + ".correct", com.google.firebase.firestore.FieldValue.increment(1));
        } else {
            update.put(letter + ".wrong", com.google.firebase.firestore.FieldValue.increment(1));
        }

        // Optional: track total attempts for AI calculations
        update.put(letter + ".total", com.google.firebase.firestore.FieldValue.increment(1));

        db.collection(path)
                .document(letter)
                .set(update, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    // optional: log or callback
                    System.out.println("Updated " + letter + " for child " + childId);
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error updating " + letter + ": " + e.getMessage());
                });
    }
}