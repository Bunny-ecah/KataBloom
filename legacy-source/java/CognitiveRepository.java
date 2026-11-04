package com.example.myproject.data;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CognitiveRepository {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface Callback {
        void onResult(
                double readingSpeed,
                double responseTime,
                double fatigue,
                Map<String, Double> confusion
        );
    }

    public void load(String userId, String childId, Callback cb) {

        db.collection("users")
                .document(userId)
                .collection("children")
                .document(childId)
                .get()
                .addOnSuccessListener(doc -> {

                    double reading = getDouble(doc, "cognition_readingSpeed");
                    double response = getDouble(doc, "cognition_responseTime");
                    double fatigue = getDouble(doc, "cognition_fatigue");

                    Map<String, Double> confusion = new HashMap<>();

                    confusion.put("b_d", getDouble(doc, "confusion_b_d"));
                    confusion.put("p_q", getDouble(doc, "confusion_p_q"));
                    confusion.put("m_n", getDouble(doc, "confusion_m_n"));

                    cb.onResult(reading, response, fatigue, confusion);
                });
    }

    private double getDouble(com.google.firebase.firestore.DocumentSnapshot doc, String key) {
        Double v = doc.getDouble(key);
        return v == null ? 0.0 : v;
    }
}