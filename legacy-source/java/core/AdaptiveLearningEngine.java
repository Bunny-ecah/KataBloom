package com.example.myproject.core;

public class AdaptiveLearningEngine {

    public static String getDifficulty(ChildLearningProfile profile, String letter) {

        int accuracy = profile.getAccuracy(letter);

        if (accuracy < 40) return "easy";
        if (accuracy < 75) return "medium";
        return "hard";
    }
}