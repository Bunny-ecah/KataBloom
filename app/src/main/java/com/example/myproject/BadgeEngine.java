package com.example.myproject;

public class BadgeEngine {

    public static String getBadge(int phonicsDone, double quiz, double speech) {

        if (phonicsDone >= 26 && quiz >= 80 && speech >= 80) {
            return "🏆 Master Learner";
        }

        if (phonicsDone >= 20) {
            return "🥇 Gold Explorer";
        }

        if (phonicsDone >= 10) {
            return "🥈 Silver Learner";
        }

        if (quiz >= 80) {
            return "🧠 Quiz Master";
        }

        if (speech >= 80) {
            return "🗣 Speaker Star";
        }

        return "🌱 Beginner";
    }


        public static String getBadge(int phonics, int quiz, int speech, int completed) {

            int avg = (phonics + quiz + speech) / 3;

            if (completed >= 26 && avg >= 90) {
                return "🏆 Master Reader";
            }

            if (completed >= 15 && avg >= 70) {
                return "⭐ Rising Star";
            }

            if (avg >= 50) {
                return "🥉 Beginner";
            }

            return "No Badge Yet";
        }

}