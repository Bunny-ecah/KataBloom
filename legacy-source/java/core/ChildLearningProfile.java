package com.example.myproject.core;

import java.util.HashMap;
import java.util.Map;

public class ChildLearningProfile {

    public Map<String, Integer> correctMap = new HashMap<>();
    public Map<String, Integer> wrongMap = new HashMap<>();

    public int totalCorrect;
    public int totalWrong;

    // update per letter
    public void addResult(String letter, boolean correct) {

        if (correct) {
            correctMap.put(letter,
                    com.example.myproject.MapValues.get(correctMap, letter, 0) + 1);
            totalCorrect++;
        } else {
            wrongMap.put(letter,
                    com.example.myproject.MapValues.get(wrongMap, letter, 0) + 1);
            totalWrong++;
        }
    }

    public int getAccuracy(String letter) {

        int c = com.example.myproject.MapValues.get(correctMap, letter, 0);
        int w = com.example.myproject.MapValues.get(wrongMap, letter, 0);

        if (c + w == 0) return 50;

        return (c * 100) / (c + w);
    }
}