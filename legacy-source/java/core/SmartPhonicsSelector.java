package com.example.myproject.core;

import java.util.Map;

public class SmartPhonicsSelector {

    public static String getWeakestLetter(ChildLearningProfile profile) {

        String worstLetter = "A";
        int worstScore = 999;

        for (Map.Entry<String, Integer> entry : profile.wrongMap.entrySet()) {

            if (entry.getValue() > worstScore) {
                worstScore = entry.getValue();
                worstLetter = entry.getKey();
            }
        }

        return worstLetter;
    }
}