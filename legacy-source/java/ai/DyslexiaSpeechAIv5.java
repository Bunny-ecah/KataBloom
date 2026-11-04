package com.example.myproject.ai;

import java.util.HashMap;
import java.util.Map;

public class DyslexiaSpeechAIv5 {

    public static class Result {
        public int accuracy;
        public String errorType;
        public String weakPattern;
        public boolean isDyslexicPattern;
    }

    public static Result analyze(String spoken, String target) {

        Result r = new Result();

        spoken = spoken.toLowerCase();
        target = target.toLowerCase();

        r.accuracy = calculateAccuracy(spoken, target);

        r.errorType = detectErrorType(spoken, target);
        r.weakPattern = detectPattern(spoken, target);

        r.isDyslexicPattern =
                r.errorType.equals("phoneme_confusion") ||
                        r.errorType.equals("omission");

        return r;
    }

    // ---------------- ACCURACY ----------------
    private static int calculateAccuracy(String s, String t) {

        if (s.equals(t)) return 100;

        int match = 0;
        int len = Math.min(s.length(), t.length());

        for (int i = 0; i < len; i++) {
            if (s.charAt(i) == t.charAt(i)) match++;
        }

        return (int) ((match * 100.0) / t.length());
    }

    // ---------------- ERROR DETECTION ----------------
    private static String detectErrorType(String s, String t) {

        if (s.equals(t)) return "correct";

        if (Math.abs(s.length() - t.length()) >= 2)
            return "omission";

        if (hasPhonemeSwap(s, t))
            return "phoneme_confusion";

        return "general_mistake";
    }

    // ---------------- DYSLEXIA PATTERN ----------------
    private static boolean hasPhonemeSwap(String s, String t) {

        Map<Character, Character> pairs = new HashMap<>();
        pairs.put('b', 'd');
        pairs.put('d', 'b');
        pairs.put('p', 'q');
        pairs.put('q', 'p');
        pairs.put('m', 'n');
        pairs.put('n', 'm');

        for (int i = 0; i < Math.min(s.length(), t.length()); i++) {
            char a = s.charAt(i);
            char b = t.charAt(i);

            if (pairs.containsKey(a) && pairs.get(a) == b) {
                return true;
            }
        }
        return false;
    }

    // ---------------- PATTERN LABEL ----------------
    private static String detectPattern(String s, String t) {

        if (s.length() < t.length())
            return "sound_missing";

        if (s.length() > t.length())
            return "sound_added";

        if (hasPhonemeSwap(s, t))
            return "b_d_or_p_q_confusion";

        return "normal";
    }
}