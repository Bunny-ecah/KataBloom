package com.example.myproject;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class LearningEngine {

    public static class Recommendation {
        public String module;     // phonics / speech / quiz
        public String difficulty; // easy / medium / hard
        public String reason;
    }

    public static Recommendation decide(
            Map<String, Integer> phonics,
            Map<String, Map<String, Integer>> speech,
            Map<String, Map<String, Integer>> quiz
    ) {

        Recommendation r = new Recommendation();

        // 1. PHONICS LOGIC (A-Z progression)
        int phonicsWrong = com.example.myproject.MapValues.get(phonics, "wrong", 0);

        if (phonicsWrong > 5) {
            r.module = "phonics";
            r.difficulty = "easy";
            r.reason = "Weak phonics detected";
            return r;
        }

        // 2. SPEECH LOGIC
        int speechEasyWrong = com.example.myproject.MapValues.get(speech.get("easy"), "wrong", 0);
        int speechMedWrong = com.example.myproject.MapValues.get(speech.get("medium"), "wrong", 0);

        if (speechEasyWrong > 3) {
            r.module = "speech";
            r.difficulty = "easy";
            r.reason = "Need basic pronunciation practice";
            return r;
        }

        if (speechMedWrong > 3) {
            r.module = "speech";
            r.difficulty = "medium";
            r.reason = "Struggling with phrases";
            return r;
        }

        // 3. QUIZ LOGIC
        int quizHardWrong = com.example.myproject.MapValues.get(quiz.get("hard"), "wrong", 0);

        if (quizHardWrong > 2) {
            r.module = "quiz";
            r.difficulty = "medium";
            r.reason = "Concept weakness detected";
            return r;
        }

        // DEFAULT PROGRESSION
        r.module = "speech";
        r.difficulty = "hard";
        r.reason = "Ready for challenge level";

        return r;
    }
}