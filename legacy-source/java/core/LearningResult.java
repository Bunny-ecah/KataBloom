package com.example.myproject.core;

public class LearningResult {

    public String skill;          // phonics / speech / quiz
    public String answer;         // what child said
    public boolean correct;

    public String difficulty;    // easy / medium / hard
    public int score;

    public String feedback;

    public LearningResult(String skill, String answer, boolean correct,
                          String difficulty, int score, String feedback) {
        this.skill = skill;
        this.answer = answer;
        this.correct = correct;
        this.difficulty = difficulty;
        this.score = score;
        this.feedback = feedback;
    }
}