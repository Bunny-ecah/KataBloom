package com.example.myproject.core;

public class RewardManager {

    private int stars = 0;
    private int streak = 0;

    public void correctAnswer() {
        streak++;
        stars += 2;

        if (streak % 3 == 0) stars += 3; // bonus
    }

    public void wrongAnswer() {
        streak = 0;
    }

    public int getStars() {
        return stars;
    }

    public int getStreak() {
        return streak;
    }
}