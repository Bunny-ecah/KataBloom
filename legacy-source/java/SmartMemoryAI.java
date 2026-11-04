package com.example.myproject.ai;

import java.util.HashMap;
import java.util.Map;

public class SmartMemoryAI {

    public static class Result {
        public String weakSkill;
        public String focusArea;
        public String difficulty;
    }

    public static Result analyze(
            Map<String, Integer> phonicsMemory,
            Map<String, Integer> quizMemory,
            Map<String, Integer> speechMemory,
            Map<String, Integer> mastery
    ) {

        // 1. FIND STRONGEST MEMORY WEIGHT (mistake-based)
        String weakSkill = "phonics";
        int maxMistakes = sum(phonicsMemory);

        int quizMistakes = sum(quizMemory);
        int speechMistakes = sum(speechMemory);

        if (quizMistakes > maxMistakes) {
            weakSkill = "quiz";
            maxMistakes = quizMistakes;
        }

        if (speechMistakes > maxMistakes) {
            weakSkill = "speech";
            maxMistakes = speechMistakes;
        }

        // 2. DETECT SPECIFIC FOCUS AREA
        String focusArea = detectPattern(weakSkill, phonicsMemory, quizMemory, speechMemory);

        // 3. ADAPT DIFFICULTY BASED ON MASTERY + MEMORY
        int m = com.example.myproject.MapValues.get(mastery, weakSkill, 0);

        String difficulty;
        if (m < 40 || maxMistakes > 5) difficulty = "easy";
        else if (m < 75) difficulty = "medium";
        else difficulty = "hard";

        Result r = new Result();
        r.weakSkill = weakSkill;
        r.focusArea = focusArea;
        r.difficulty = difficulty;

        return r;
    }

    private static int sum(Map<String, Integer> map) {
        int total = 0;
        for (int v : map.values()) total += v;
        return total;
    }

    private static String detectPattern(
            String skill,
            Map<String, Integer> phonics,
            Map<String, Integer> quiz,
            Map<String, Integer> speech
    ) {

        Map<String, Integer> target =
                skill.equals("phonics") ? phonics :
                        skill.equals("quiz") ? quiz : speech;

        String worst = "general";

        int max = 0;
        for (String k : target.keySet()) {
            if (target.get(k) > max) {
                max = target.get(k);
                worst = k;
            }
        }

        return worst;
    }
}