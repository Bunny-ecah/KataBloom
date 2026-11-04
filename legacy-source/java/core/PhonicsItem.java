package com.example.myproject.core;

public class PhonicsItem {
    public String bigLetter;
    public String smallLetter;
    public String sound;
    public String audioText;
    public String word;
    public String level;
    public String id;

    public PhonicsItem() {} // Firestore requires empty constructor

    public PhonicsItem(String bigLetter, String smallLetter, String sound, String audioText, String word, String level) {
        this.bigLetter = bigLetter;
        this.smallLetter = smallLetter;
        this.sound = sound;
        this.audioText = audioText;
        this.word = word;
        this.level = level;
    }
}