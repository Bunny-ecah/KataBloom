package com.example.myproject.core;

public class DifficultyManager {

    public static String getLevel(int correct, int wrong) {

        int score = correct - wrong;

        if (score < 3) return "easy";
        if (score < 7) return "medium";
        return "hard";
    }
}