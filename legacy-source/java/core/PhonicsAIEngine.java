package com.example.myproject.core;

public class PhonicsAIEngine {

    // Determine level based on previous correct/wrong and streak
    public static String getLevel(int correct, int wrong, int streak) {
        int total = correct + wrong;
        if (total < 5) return "easy";          // first letters
        if (streak >= 3) return "medium";      // reward streak
        if (wrong > correct) return "easy";    // struggling
        return "hard";                          // otherwise
    }
}