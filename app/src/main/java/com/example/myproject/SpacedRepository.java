package com.example.myproject.data;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SpacedRepository {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface Callback {
        void onResult(Map<String, Long> schedule,
                      Map<String, Double> performance);
    }

    public void load(String userId, String childId, Callback cb) {

        db.collection("users")
                .document(userId)
                .collection("children")
                .document(childId)
                .get()
                .addOnSuccessListener(doc -> {

                    Map<String, Long> schedule = new HashMap<>();
                    Map<String, Double> performance = new HashMap<>();

                    schedule.put("phonics_b_d",
                            doc.getLong("sr_phonics_b_d_next") != null ?
                                    doc.getLong("sr_phonics_b_d_next") : 0);

                    schedule.put("quiz_spelling",
                            doc.getLong("sr_quiz_spelling_next") != null ?
                                    doc.getLong("sr_quiz_spelling_next") : 0);

                    performance.put("phonics_b_d",
                            getDouble(doc, "sr_phonics_b_d_score"));

                    performance.put("quiz_spelling",
                            getDouble(doc, "sr_quiz_spelling_score"));

                    cb.onResult(schedule, performance);
                });
    }

    private double getDouble(com.google.firebase.firestore.DocumentSnapshot doc, String key) {
        Double v = doc.getDouble(key);
        return v == null ? 0.5 : v;
    }
}