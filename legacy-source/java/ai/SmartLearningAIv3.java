package com.example.myproject.ai;

import java.util.Map;

public class SmartLearningAIv3 {

    public static class Result {
        public String skill;
        public String difficulty;
        public String reason;
        public int confidence;
    }

    public static Result analyze(
            Map<String, Integer> memory,
            Map<String, Integer> mastery
    ) {

        String weakestSkill = findWeakSkill(memory);
        int masteryLevel = com.example.myproject.MapValues.get(mastery, weakestSkill, 0);

        String difficulty = decideDifficulty(masteryLevel);

        Result r = new Result();
        r.skill = weakestSkill;
        r.difficulty = difficulty;
        r.reason = generateReason(memory, weakestSkill);
        r.confidence = calculateConfidence(memory, masteryLevel);

        return r;
    }

    // ---------------- WEAKNESS DETECTION ----------------
    private static String findWeakSkill(Map<String, Integer> memory) {

        int phonics = com.example.myproject.MapValues.get(memory, "phonics_wrong", 0);
        int quiz = com.example.myproject.MapValues.get(memory, "quiz_wrong", 0);
        int speech = com.example.myproject.MapValues.get(memory, "speech_wrong", 0);

        if (phonics >= quiz && phonics >= speech) return "phonics";
        if (quiz >= phonics && quiz >= speech) return "quiz";
        return "speech";
    }

    // ---------------- DIFFICULTY ENGINE ----------------
    private static String decideDifficulty(int mastery) {

        if (mastery < 30) return "easy";
        if (mastery < 70) return "medium";
        return "hard";
    }

    // ---------------- EXPLANATION ENGINE ----------------
    private static String generateReason(Map<String, Integer> memory, String skill) {

        if (skill.equals("phonics")) {
            if (com.example.myproject.MapValues.get(memory, "b_d", 0) > 3)
                return "Letter confusion detected (b/d)";
            return "General phonics weakness";
        }

        if (skill.equals("quiz")) {
            return "Low quiz accuracy detected";
        }

        return "Speech pronunciation needs improvement";
    }

    // ---------------- CONFIDENCE SCORE ----------------
    private static int calculateConfidence(Map<String, Integer> memory, int mastery) {

        int totalAttempts =
                com.example.myproject.MapValues.get(memory, "phonics_total", 0) +
                        com.example.myproject.MapValues.get(memory, "quiz_total", 0) +
                        com.example.myproject.MapValues.get(memory, "speech_total", 0);

        if (totalAttempts < 5) return 40;
        if (mastery < 30) return 80;
        return 60;
    }
}