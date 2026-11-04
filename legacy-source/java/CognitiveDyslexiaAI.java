package com.example.myproject.ai;

import java.util.Map;

public class CognitiveDyslexiaAI {

    public static class CognitiveResult {
        public String focusSkill;
        public String confusionType;
        public String difficulty;
        public String cognitiveLoad;
        public double riskLevel;
    }

    public static CognitiveResult analyze(
            double readingSpeedWPM,
            double avgResponseTime,
            double fatigue,
            Map<String, Double> confusion
    ) {

        CognitiveResult r = new CognitiveResult();

        // 🧠 1. DETECT SLOW LEARNING STATE
        if (readingSpeedWPM < 40) {
            r.cognitiveLoad = "high";
        } else if (readingSpeedWPM < 80) {
            r.cognitiveLoad = "medium";
        } else {
            r.cognitiveLoad = "low";
        }

        // 🧠 2. DETECT FATIGUE IMPACT
        r.riskLevel = fatigue + (avgResponseTime / 5000.0);

        // 🧠 3. FIND STRONGEST CONFUSION PATTERN
        String worst = "none";
        double max = 0;

        for (String key : confusion.keySet()) {
            if (confusion.get(key) > max) {
                max = confusion.get(key);
                worst = key;
            }
        }

        r.confusionType = worst;

        // 🧠 4. MAP FOCUS SKILL
        if (worst.contains("b_d")) {
            r.focusSkill = "Letter Discrimination (b/d)";
        } else if (worst.contains("p_q")) {
            r.focusSkill = "Letter Discrimination (p/q)";
        } else {
            r.focusSkill = "General Phonics Reinforcement";
        }

        // 🧠 5. ADAPT DIFFICULTY BASED ON COGNITION
        if (r.riskLevel > 1.2 || fatigue > 0.7) {
            r.difficulty = "very_easy";
        } else if (r.riskLevel > 0.8) {
            r.difficulty = "easy";
        } else if (r.riskLevel > 0.5) {
            r.difficulty = "medium";
        } else {
            r.difficulty = "adaptive_hard";
        }

        return r;
    }

    public static class Result {
        public String riskLevel;        // LOW / MEDIUM / HIGH
        public String pattern;          // b/d confusion etc
        public String recommendation;   // action
        public double confidence;
    }

    public static Result analyze(
            Map<String, Integer> memory,
            Map<String, Double> cognitive
    ) {

        Result r = new Result();

        String pattern = detectPattern(memory);
        double riskScore = computeRisk(memory, cognitive);

        r.pattern = pattern;
        r.riskLevel = classifyRisk(riskScore);
        r.confidence = Math.min(0.95, riskScore);

        r.recommendation = generateIntervention(pattern, r.riskLevel);

        return r;
    }

    // ---------------- PATTERN DETECTION ----------------
    private static String detectPattern(Map<String, Integer> memory) {

        if (com.example.myproject.MapValues.get(memory, "b_d", 0) >= 3)
            return "Letter reversal (b/d)";

        if (com.example.myproject.MapValues.get(memory, "p_q", 0) >= 3)
            return "Letter reversal (p/q)";

        if (com.example.myproject.MapValues.get(memory, "word_skip", 0) >= 4)
            return "Skipping words during reading";

        if (com.example.myproject.MapValues.get(memory, "slow_response", 0) >= 5)
            return "Slow cognitive processing";

        return "No strong dyslexia pattern detected";
    }

    // ---------------- RISK ENGINE ----------------
    private static double computeRisk(
            Map<String, Integer> memory,
            Map<String, Double> cognitive
    ) {

        double reversal =
                com.example.myproject.MapValues.get(memory, "b_d", 0) +
                        com.example.myproject.MapValues.get(memory, "p_q", 0);

        double delay = com.example.myproject.MapValues.get(cognitive, "response_delay", 1.0);

        double repetition = com.example.myproject.MapValues.get(memory, "repeat_error", 0);

        return (reversal * 0.4) + (delay * 0.3) + (repetition * 0.3);
    }

    // ---------------- CLASSIFICATION ----------------
    private static String classifyRisk(double score) {

        if (score < 3) return "LOW";
        if (score < 7) return "MEDIUM";
        return "HIGH";
    }

    // ---------------- INTERVENTION ENGINE ----------------
    private static String generateIntervention(String pattern, String risk) {

        if (risk.equals("HIGH")) {

            if (pattern.contains("b/d"))
                return "Use b/d visual training + tracing";

            if (pattern.contains("p/q"))
                return "Use mirror-letter correction exercises";

            return "Slow reading + phonics reinforcement";
        }

        if (risk.equals("MEDIUM")) {
            return "Guided phonics + repetition exercises";
        }

        return "Normal learning path";
    }

}