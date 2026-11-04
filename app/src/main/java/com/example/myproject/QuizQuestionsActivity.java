package com.example.myproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class QuizQuestionsActivity extends AppCompatActivity {

    TextView tvQuestionNumber, tvQuestion, tvFeedback, tvProgress, tvScore, tvStars;
    Button btnOptionA, btnOptionB, btnOptionC, btnOptionD, btnNext;

    List<QuizQuestion> questions = new ArrayList<>();
    int currentIndex = 0;
    int score = 0;
    int stars = 0;
    boolean answered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_questions);

        // Bind views
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvFeedback = findViewById(R.id.tvFeedback);
        tvProgress = findViewById(R.id.tvProgress);
        tvScore = findViewById(R.id.tvScore);
        tvStars = findViewById(R.id.tvStars);

        btnOptionA = findViewById(R.id.btnOptionA);
        btnOptionB = findViewById(R.id.btnOptionB);
        btnOptionC = findViewById(R.id.btnOptionC);
        btnOptionD = findViewById(R.id.btnOptionD);
        btnNext = findViewById(R.id.btnNext);

        // Load dummy questions for now
        loadQuestions();

        // Display first question
        showQuestion();

        // Option buttons
        btnOptionA.setOnClickListener(v -> checkAnswer("A"));
        btnOptionB.setOnClickListener(v -> checkAnswer("B"));
        btnOptionC.setOnClickListener(v -> checkAnswer("C"));
        btnOptionD.setOnClickListener(v -> checkAnswer("D"));

        btnNext.setOnClickListener(v -> nextQuestion());
    }

    private void loadQuestions() {
        // Dummy example; later can load from Firestore
        questions.add(new QuizQuestion("What is the first letter of Apple?", "A"));
        questions.add(new QuizQuestion("Which letter comes after B?", "C"));
        questions.add(new QuizQuestion("Select the letter in 'Cat'", "C"));
    }

    private void showQuestion() {
        if (currentIndex >= questions.size()) {
            tvFeedback.setText("Quiz finished! Score: " + score + ", Stars: " + stars);
            btnNext.setEnabled(false);
            return;
        }

        QuizQuestion q = questions.get(currentIndex);
        tvQuestionNumber.setText("Question " + (currentIndex + 1));
        tvQuestion.setText(q.question);

        tvFeedback.setText("");
        tvProgress.setText("Progress: " + (currentIndex + 1) + "/" + questions.size());
        tvScore.setText("Score: " + score);
        tvStars.setText("⭐ Stars: " + stars);
    }

    private void checkAnswer(String selected) {
        if (answered || currentIndex >= questions.size()) return;
        answered = true;
        QuizQuestion q = questions.get(currentIndex);

        if (selected.equals(q.correctAnswer)) {
            score += 10;
            tvFeedback.setText("🎉 Correct!");
            if (score % 30 == 0) {
                stars++;
            }
        } else {
            tvFeedback.setText("💡 Try again!");
        }

        tvScore.setText("Score: " + score);
        tvStars.setText("⭐ Stars: " + stars);
    }

    private void nextQuestion() {
        if (!answered || currentIndex >= questions.size()) return;
        answered = false;
        currentIndex++;
        showQuestion();
    }

    // Inner class for quiz question
    static class QuizQuestion {
        String question;
        String correctAnswer;

        QuizQuestion(String question, String correctAnswer) {
            this.question = question;
            this.correctAnswer = correctAnswer;
        }
    }
}