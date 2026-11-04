package com.example.myproject.data;

import android.content.Context;

import com.example.myproject.cache.AICacheManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AIRepository {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface Callback {
        void onResult(Map<String, Integer> memory,
                      Map<String, Integer> mastery,
                      boolean fromCache);
    }

    public void load(Context context, String userId, String childId, Callback cb) {

        String cacheKey = childId + "_ai";

        String cached = AICacheManager.get(context, cacheKey);

        if (cached != null) {
            cb.onResult(fakeMemory(), fakeMastery(), true);
            return;
        }

        db.collection("users")
                .document(userId)
                .collection("children")
                .document(childId)
                .get()
                .addOnSuccessListener(doc -> {

                    Map<String, Integer> memory = new HashMap<>();
                    Map<String, Integer> mastery = new HashMap<>();

                    memory.put("phonics_total", get(doc, "phonics_wrong"));
                    memory.put("quiz_total", get(doc, "quiz_wrong"));
                    memory.put("speech_total", get(doc, "speech_wrong"));

                    memory.put("b_d", get(doc, "memory_b_d"));
                    memory.put("p_q", get(doc, "memory_p_q"));
                    memory.put("pronunciation", get(doc, "memory_speech"));

                    mastery.put("phonics", get(doc, "phonics_mastery"));
                    mastery.put("quiz", get(doc, "quiz_mastery"));
                    mastery.put("speech", get(doc, "speech_mastery"));

                    AICacheManager.save(context, cacheKey, "cached");

                    cb.onResult(memory, mastery, false);
                });
    }

    private int get(com.google.firebase.firestore.DocumentSnapshot doc, String key) {
        Long v = doc.getLong(key);
        return v == null ? 0 : v.intValue();
    }

    private Map<String, Integer> fakeMemory() {
        return new HashMap<>();
    }

    private Map<String, Integer> fakeMastery() {
        return new HashMap<>();
    }
}