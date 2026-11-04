package com.example.myproject.core;

public class QuizItem {
    public String question;
    public String optionA;
    public String optionB;
    public String optionC;
    public String optionD;
    public String correctOption;
    public String level;
    public String id;

    public QuizItem() {} // Firestore requires empty constructor
}