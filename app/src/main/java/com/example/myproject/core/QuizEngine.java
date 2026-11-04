package com.example.myproject.core;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class QuizEngine {

    private FirebaseFirestore db;

    public QuizEngine() {
        db = FirebaseFirestore.getInstance();
    }

    public interface QuizLoadCallback {
        void onLoaded(List<QuizItem> questions);
        void onError(String error);
    }

    public void loadQuestions(String childId, String level, QuizLoadCallback callback) {
        // Firestore structure: quiz_questions collection
        db.collection("quiz_questions")
                .whereEqualTo("level", level)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<QuizItem> questions = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        QuizItem item = doc.toObject(QuizItem.class);
                        item.id = doc.getId();
                        questions.add(item);
                    }
                    callback.onLoaded(questions);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}