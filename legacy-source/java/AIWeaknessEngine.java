package com.example.myproject;

public class AIWeaknessEngine {

    public static String getWeakness(double phonics, double quiz, double speech) {

        if (phonics < 50 && quiz < 50) {
            return "Letter Recognition + Understanding Difficulty";
        }

        if (speech < 50) {
            return "Pronunciation & Speech Clarity Issue";
        }

        if (quiz < 60) {
            return "Comprehension Weakness";
        }

        if (phonics < 70) {
            return "Letter Familiarity Needs Improvement";
        }

        return "Strong Performance";
    }


        public static String getWeakness(int phonics, int quiz, int speech) {

            if (speech < phonics && speech < quiz) {
                return "Speech Practice";
            }

            if (phonics < quiz) {
                return "Phonics / Letter Recognition";
            }

            if (quiz < phonics) {
                return "Quiz Understanding";
            }

            return "Balanced Performance";
        }

}