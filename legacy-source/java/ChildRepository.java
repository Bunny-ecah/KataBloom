package com.example.myproject.data;

import com.google.firebase.firestore.FirebaseFirestore;

public class ChildRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void getChild(String userId, String childId, Callback callback) {

        db.collection("users")
                .document(userId)
                .collection("children")
                .document(childId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) return;

                    String name = doc.getString("name");
                    String age = doc.getString("age");

                    Long streak = doc.getLong("streak");

                    callback.onResult(name, age,
                            streak != null ? streak.intValue() : 0);
                });
    }

    public interface Callback {
        void onResult(String name, String age, int streak);
    }
}