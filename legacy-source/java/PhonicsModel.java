package com.example.myproject;

public class PhonicsModel {

    public String letter;
    public String sound;
    public String word;
    public boolean completed;

    public PhonicsModel() {}

    public PhonicsModel(String letter, String sound, String word, boolean completed) {
        this.letter = letter;
        this.sound = sound;
        this.word = word;
        this.completed = completed;
    }
}