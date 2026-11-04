package com.example.myproject.ai;

import java.util.HashMap;
import java.util.Map;

public class AIRecommender {

    public static class Recommendation {
        public String skill;
        public String lesson;
        public String difficulty;
    }

    public static Recommendation generate(
            int phonicsCorrect, int phonicsWrong,
            int quizCorrect, int quizWrong,
            int speechCorrect, int speechWrong
    ) {

        Map<String, Double> mastery = new HashMap<>();

        mastery.put("phonics", score(phonicsCorrect, phonicsWrong));
        mastery.put("quiz", score(quizCorrect, quizWrong));
        mastery.put("speech", score(speechCorrect, speechWrong));

        // 1. FIND WEAKEST SKILL
        String weakest = "phonics";
        double min = mastery.get("phonics");

        for (String key : mastery.keySet()) {
            if (mastery.get(key) < min) {
                min = mastery.get(key);
                weakest = key;
            }
        }

        // 2. DETERMINE DIFFICULTY
        String difficulty;
        if (min < 40) difficulty = "easy";
        else if (min < 75) difficulty = "medium";
        else difficulty = "hard";

        // 3. MAP LESSON
        String lesson = getLesson(weakest);

        Recommendation r = new Recommendation();
        r.skill = weakest;
        r.lesson = lesson;
        r.difficulty = difficulty;

        return r;
    }

    private static double score(int correct, int wrong) {
        int total = correct + wrong;
        if (total == 0) return 0;
        return (correct * 100.0) / total;
    }

    private static String getLesson(String skill) {

        switch (skill) {
            case "phonics":
                return "Phonics - Letter Recognition";
            case "quiz":
                return "Quiz - Word Matching";
            case "speech":
                return "Speech - Pronunciation Practice";
            default:
                return "General Practice";
        }
    }
}