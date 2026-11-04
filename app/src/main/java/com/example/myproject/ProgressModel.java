package com.example.myproject;

public class ProgressModel {

    public int phonicsCompleted;
    public int phonicsTotal;
    public String weakness;
    public String badge;
    public double quizScore;
    public double speechScore;
    public int phonicsScore;
    public int completedLetters;

    public ProgressModel() {

    }

    public ProgressModel(int phonicsCompleted, int phonicsTotal,
                         double quizScore, double speechScore, int phonicsScore, int completedLetters) {
        this.phonicsCompleted = phonicsCompleted;
        this.phonicsTotal = phonicsTotal;
        this.quizScore = quizScore;
        this.speechScore = speechScore;
        this.phonicsScore = phonicsScore;
        this.completedLetters = completedLetters;


    }

    public int getPhonicsPercent() {
        if (phonicsTotal == 0) return 0;
        return (phonicsCompleted * 100) / phonicsTotal;
    }
}