package com.example.myproject.phonics;

import java.util.List;
import java.util.Random;

public class PhonicsEngine {

    private final char[] letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private int index = 0;

    public PhonicsItem getNext() {

        if (index >= letters.length) index = 0;

        char c = letters[index];

        PhonicsItem item = new PhonicsItem(
                String.valueOf(c),
                getSound(c),
                getExample(c)
        );

        index++;
        return item;
    }

    private String getSound(char c) {
        switch (c) {
            case 'A': return "/æ/";
            case 'B': return "/b/";
            case 'C': return "/k/";
            default: return "/" + c + "/";
        }
    }

    private String getExample(char c) {
        switch (c) {
            case 'A': return "Apple";
            case 'B': return "Ball";
            case 'C': return "Cat";
            default: return "Word";
        }
    }
}