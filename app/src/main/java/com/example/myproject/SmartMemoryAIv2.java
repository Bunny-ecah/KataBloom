package com.example.myproject.ai;

import java.util.Map;

public class SmartMemoryAIv2 {

    public static class Result {
        public String skill;
        public String focus;
        public String difficulty;
        public boolean fromCache;
    }

    public static Result compute(
            Map<String, Integer> memory,
            Map<String, Integer> mastery
    ) {

        String weakest = "phonics";

        int phonicsMistake = com.example.myproject.MapValues.get(memory, "phonics_errors", 0);
        int quizMistake = com.example.myproject.MapValues.get(memory, "quiz_errors", 0);
        int speechMistake = com.example.myproject.MapValues.get(memory, "speech_errors", 0);

        if (quizMistake > phonicsMistake && quizMistake >= speechMistake) {
            weakest = "quiz";
        } else if (speechMistake > phonicsMistake) {
            weakest = "speech";
        }

        int m = com.example.myproject.MapValues.get(mastery, weakest, 0);

        String difficulty;
        if (m < 40) difficulty = "easy";
        else if (m < 75) difficulty = "medium";
        else difficulty = "hard";

        Result r = new Result();
        r.skill = weakest;
        r.focus = detectFocus(memory, weakest);
        r.difficulty = difficulty;
        r.fromCache = false;

        return r;
    }

    private static String detectFocus(Map<String, Integer> memory, String skill) {

        if (skill.equals("phonics")) {

            if (com.example.myproject.MapValues.get(memory, "b_d_confusion", 0) > 3)
                return "b-d confusion";

            if (com.example.myproject.MapValues.get(memory, "p_q_confusion", 0) > 3)
                return "p-q confusion";
        }

        if (skill.equals("speech")) {

            if (com.example.myproject.MapValues.get(memory, "pronunciation_delay", 0) > 3)
                return "pronunciation delay";
        }

        return "general practice";
    }
}