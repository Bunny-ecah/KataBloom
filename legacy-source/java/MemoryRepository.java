package com.example.myproject.data;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MemoryRepository {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface Callback {
        void onResult(
                Map<String, Integer> phonics,
                Map<String, Integer> quiz,
                Map<String, Integer> speech,
                Map<String, Integer> mastery
        );
    }

    public void loadMemory(String userId, String childId, Callback callback) {

        db.collection("users")
                .document(userId)
                .collection("children")
                .document(childId)
                .get()
                .addOnSuccessListener(doc -> {

                    Map<String, Integer> phonics = new HashMap<>();
                    Map<String, Integer> quiz = new HashMap<>();
                    Map<String, Integer> speech = new HashMap<>();
                    Map<String, Integer> mastery = new HashMap<>();

                    // phonics memory
                    phonics.put("b_d", getInt(doc, "memory.phonics.b_d_confusion"));
                    phonics.put("p_q", getInt(doc, "memory.phonics.p_q_confusion"));

                    // quiz memory
                    quiz.put("spelling", getInt(doc, "memory.quiz.spelling_errors"));

                    // speech memory
                    speech.put("pronunciation", getInt(doc, "memory.speech.pronunciation_errors"));

                    // mastery
                    mastery.put("phonics", getInt(doc, "mastery.phonics"));
                    mastery.put("quiz", getInt(doc, "mastery.quiz"));
                    mastery.put("speech", getInt(doc, "mastery.speech"));

                    callback.onResult(phonics, quiz, speech, mastery);
                });
    }

    private int getInt(com.google.firebase.firestore.DocumentSnapshot doc, String key) {
        Long v = doc.getLong(key);
        return v == null ? 0 : v.intValue();
    }
}