package com.example.myproject.ai;

import java.util.Map;

public class SpacedRepetitionEngine {

    public static String nextReviewSkill(Map<String, Integer> mastery) {

        String weakest = "phonics";
        int min = com.example.myproject.MapValues.get(mastery, "phonics", 0);

        if (com.example.myproject.MapValues.get(mastery, "quiz", 0) < min) {
            weakest = "quiz";
            min = mastery.get("quiz");
        }

        if (com.example.myproject.MapValues.get(mastery, "speech", 0) < min) {
            weakest = "speech";
        }

        return weakest;
    }
}