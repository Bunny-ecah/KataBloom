package com.example.myproject;

public class SpeechItem {
    public String word;
    public String hint;
    public String level;

    public SpeechItem() {} // Firestore requires empty constructor

    public SpeechItem(String word, String hint, String level) {
        this.word = word;
        this.hint = hint;
        this.level = level;
    }
}