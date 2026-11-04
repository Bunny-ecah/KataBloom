package com.example.myproject;


import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myproject.core.QuizEngine;
import com.example.myproject.core.QuizItem;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private TextView btnBack;
    private TextView tvQuizTitle, tvLevelInfo, tvQuestionNumber, tvQuestion;
    private TextView tvFeedback, tvProgress, tvScore, tvStars;
    private Button btnA, btnB, btnC, btnD, btnNext;

    private QuizEngine quizEngine;
    private List<QuizItem> currentQuestions;
    private FirebaseFirestore db;

    private int currentIndex = 0;
    private int score = 0;
    private int stars = 0;

    private boolean answered = false;

    private String level;
    private String childId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        db = FirebaseFirestore.getInstance();

        userId = ChildSessionManager.userId;

        level = getIntent().getStringExtra("level");
        childId = getIntent().getStringExtra("childId");

        if (level == null || level.isEmpty()) {
            level = "easy";
        }

        if (childId == null || childId.isEmpty()) {
            childId = ChildSessionManager.childId;
        }

        if (childId == null || childId.isEmpty()) {
            Toast.makeText(this, "Please select a child first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupClicks();

        quizEngine = new QuizEngine();
        loadQuestions(childId, level);
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        tvQuizTitle = findViewById(R.id.tvQuizTitle);
        tvLevelInfo = findViewById(R.id.tvLevelInfo);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestion = findViewById(R.id.tvQuestion);

        tvFeedback = findViewById(R.id.tvFeedback);
        tvProgress = findViewById(R.id.tvProgress);
        tvScore = findViewById(R.id.tvScore);
        tvStars = findViewById(R.id.tvStars);

        btnA = findViewById(R.id.btnOptionA);
        btnB = findViewById(R.id.btnOptionB);
        btnC = findViewById(R.id.btnOptionC);
        btnD = findViewById(R.id.btnOptionD);
        btnNext = findViewById(R.id.btnNext);

        tvLevelInfo.setText("Level: " + level);
        tvFeedback.setText("Loading questions...");
        tvProgress.setText("Progress: 0/0");
        tvScore.setText("Score: 0");
        tvStars.setText("⭐ Stars: 0");

        btnNext.setEnabled(false);
        btnNext.setText("Next Question");
    }

    private void setupClicks() {
        btnBack.setOnClickListener(v -> finish());

        btnNext.setOnClickListener(v -> moveNext());
    }

    private void loadQuestions(String childId, String level) {
        quizEngine.loadQuestions(childId, level, new QuizEngine.QuizLoadCallback() {
            @Override
            public void onLoaded(List<QuizItem> questions) {
                currentQuestions = questions;
                currentIndex = 0;
                score = 0;
                stars = 0;

                if (questions == null || questions.isEmpty()) {
                    tvQuestionNumber.setText("No Question");
                    tvQuestion.setText("No quiz questions found for this level.");
                    tvFeedback.setText("Please add quiz questions in Firestore.");
                    tvProgress.setText("Progress: 0/0");
                    btnNext.setEnabled(false);
                    disableOptions();
                    return;
                }

                displayQuestion(currentQuestions.get(currentIndex));
            }

            @Override
            public void onError(String error) {
                Toast.makeText(
                        QuizActivity.this,
                        "Error loading questions: " + error,
                        Toast.LENGTH_LONG
                ).show();

                tvFeedback.setText("Failed to load quiz questions.");
                btnNext.setEnabled(false);
                disableOptions();
            }
        });
    }

    private void displayQuestion(QuizItem question) {
        answered = false;
        for (Button b : new Button[]{btnA,btnB,btnC,btnD}) b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.rgb(232,237,255)));

        tvQuestionNumber.setText("Question " + (currentIndex + 1));
        tvQuestion.setText(getSafeText(question.question, "No question"));

        btnA.setText(getSafeText(question.optionA, "Option A"));
        btnB.setText(getSafeText(question.optionB, "Option B"));
        btnC.setText(getSafeText(question.optionC, "Option C"));
        btnD.setText(getSafeText(question.optionD, "Option D"));

        tvFeedback.setText("Choose the correct answer");
        tvProgress.setText("Progress: " + (currentIndex + 1) + "/" + currentQuestions.size());
        tvScore.setText("Score: " + score);
        tvStars.setText("⭐ Stars: " + stars);

        enableOptions();

        btnNext.setEnabled(false);

        if (currentIndex == currentQuestions.size() - 1) {
            btnNext.setText("Finish Quiz");
        } else {
            btnNext.setText("Next Question");
        }

        btnA.setOnClickListener(v ->
                checkAnswer(
                        question,
                        "A",
                        question.optionA,
                        btnA
                ));

        btnB.setOnClickListener(v ->
                checkAnswer(question,
                        "B",
                        question.optionB,
                        btnB
                ));

        btnC.setOnClickListener(v ->
                checkAnswer(
                        question,
                        "C",
                        question.optionC,
                        btnC
                ));

        btnD.setOnClickListener(v ->
                checkAnswer(
                        question,
                        "D",
                        question.optionD,
                        btnD
                ));
    }

    private void checkAnswer(QuizItem question, String selectedOption, String selectedText,Button selectedButton) {
        if (answered) {
            return;
        }

        answered = true;

        String correctAnswer = getSafeText(question.correctOption, "").trim();

        boolean isCorrect =
                selectedOption.equalsIgnoreCase(correctAnswer)
                        || getSafeText(selectedText, "").equalsIgnoreCase(correctAnswer);

        if (isCorrect) {
            score++;
            stars++;
            showCorrectAnswer(
                    selectedButton,
                    true
            );
        }
            else{
                showCorrectAnswer(
                        selectedButton,
                        false
                );

            String answerText = correctAnswer.equalsIgnoreCase("A") ? question.optionA : correctAnswer.equalsIgnoreCase("B") ? question.optionB : correctAnswer.equalsIgnoreCase("C") ? question.optionC : correctAnswer.equalsIgnoreCase("D") ? question.optionD : correctAnswer;
            tvFeedback.setText("Good effort. Let us practise: " + answerText);
        }

        tvScore.setText("Score: " + score);
        tvStars.setText("⭐ Stars: " + stars);

        disableOptions();
        btnNext.setEnabled(true);
    }

    private void saveQuizProgress(){

        if(userId == null || childId == null || currentQuestions == null || currentQuestions.isEmpty()){
            return;
        }


        int percentage =
                (score * 100) / currentQuestions.size();



        db.collection("users")
                .document(userId)
                .collection("children")
                .document(childId)
                .update(
                        "quizScore",
                        percentage
                ).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Quiz finished, but cloud progress could not be saved. Check your connection.", Toast.LENGTH_LONG).show());

    }
    private void showCorrectAnswer(Button selectedButton, boolean correct){
        if(correct){
            selectedButton.setBackgroundTintList(
                    getColorStateList(android.R.color.holo_green_light)
            );
            tvFeedback.setText(
                    "🎉 Correct! Great job!"
            );
            tvFeedback.setTextColor(
                    getColor(android.R.color.holo_green_dark)
            );
        }else{
            selectedButton.setBackgroundTintList(
                    getColorStateList(android.R.color.holo_red_light)
            );
            tvFeedback.setText(
                    "💡 Try again!"
            );
            tvFeedback.setTextColor(
                    getColor(android.R.color.holo_red_dark)
            );
        }
    }

    private void moveNext() {
        if (currentQuestions == null || currentQuestions.isEmpty()) {
            Toast.makeText(this, "No questions available", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!answered) {
            Toast.makeText(this, "Please choose an answer first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentIndex < currentQuestions.size() - 1) {
            currentIndex++;
            displayQuestion(currentQuestions.get(currentIndex));
        } else {
            Toast.makeText(
                    this,
                    "Quiz completed! Final Score: " + score + "/" + currentQuestions.size(),
                    Toast.LENGTH_LONG
            ).show();

            saveQuizProgress();
            finish();
        }
    }

    private void enableOptions() {
        btnA.setEnabled(true);
        btnB.setEnabled(true);
        btnC.setEnabled(true);
        btnD.setEnabled(true);
    }

    private void disableOptions() {
        btnA.setEnabled(false);
        btnB.setEnabled(false);
        btnC.setEnabled(false);
        btnD.setEnabled(false);
    }

    private String getSafeText(String value, String defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value;
    }
}