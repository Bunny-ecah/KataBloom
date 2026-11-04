package com.example.myproject;

import com.google.firebase.firestore.FirebaseFirestore;

public class UserRepository {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void getUser(String userId, Callback callback) {
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    UserProfile user = doc.toObject(UserProfile.class);
                    callback.onResult(user);
                });
    }

    public void updateUser(String userId, UserProfile user) {
        db.collection("users")
                .document(userId)
                .set(user);
    }

    public interface Callback {
        void onResult(UserProfile user);
    }
}