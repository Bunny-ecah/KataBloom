package com.example.myproject.phonics;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PhonicsTracker {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // SAVE RESULT OF ONE LETTER ATTEMPT
    public void saveAttempt(
            String userId,
            String childId,
            String letter,
            boolean correct
    ) {

        String path = "users/" + userId + "/children/" + childId + "/phonics/" + letter;

        db.document(path).get().addOnSuccessListener(doc -> {

            long correctCount = 0;
            long wrongCount = 0;

            if (doc.exists()) {
                correctCount = doc.getLong("correct") != null ? doc.getLong("correct") : 0;
                wrongCount = doc.getLong("wrong") != null ? doc.getLong("wrong") : 0;
            }

            if (correct) correctCount++;
            else wrongCount++;

            long total = correctCount + wrongCount;

            long mastery = calculateMastery(correctCount, total);

            Map<String, Object> data = new HashMap<>();
            data.put("correct", correctCount);
            data.put("wrong", wrongCount);
            data.put("mastery", mastery);

            db.document(path).set(data);

        });
    }

    // SIMPLE MASTERY FORMULA
    private long calculateMastery(long correct, long total) {

        if (total == 0) return 0;

        return (correct * 100) / total;
    }
}