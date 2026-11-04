package com.example.myproject.core;

public class LearningEngine {

    public static LearningResult evaluate(
            String skill,
            String expected,
            String userAnswer,
            String difficulty
    ) {

        boolean correct = userAnswer.equalsIgnoreCase(expected);

        int baseScore = correct ? 10 : 0;

        String feedback;

        if (correct) {
            feedback = "Good job! ⭐";
        } else {
            feedback = "Try again slowly 💡";
        }

        return new LearningResult(
                skill,
                userAnswer,
                correct,
                difficulty,
                baseScore,
                feedback
        );
    }
}