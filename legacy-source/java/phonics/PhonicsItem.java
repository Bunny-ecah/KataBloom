package com.example.myproject.phonics;

public class PhonicsItem {

    public String letter;
    public String sound;
    public String example;
    public String difficulty;

    public PhonicsItem(String letter, String sound, String example) {
        this.letter = letter;
        this.sound = sound;
        this.example = example;
        this.difficulty = "easy";
    }
}