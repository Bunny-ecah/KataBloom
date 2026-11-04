package com.example.myproject;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardActivity extends BaseActivity {

    TextView tvWelcome, tvChildInfo;
    TextView tvProgressText, tvTodayGoal;
    TextView tvCompletedActivities, tvAverageScore, tvPracticeStreak;
    TextView tvRecentActivity, tvParentTip;
    ProgressBar progressLearning;

    FirebaseFirestore db;
    String userId;
    String childId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        db = FirebaseFirestore.getInstance();
        userId = ChildSessionManager.userId;
        childId = ChildSessionManager.childId;

        initViews();
        loadChildInfo();
        loadDashboardOverview();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvChildInfo = findViewById(R.id.tvChildInfo);

        progressLearning = findViewById(R.id.progressLearning);
        tvProgressText = findViewById(R.id.tvProgressText);

        tvTodayGoal = findViewById(R.id.tvTodayGoal);
        tvCompletedActivities = findViewById(R.id.tvCompletedActivities);
        tvAverageScore = findViewById(R.id.tvAverageScore);
        tvPracticeStreak = findViewById(R.id.tvPracticeStreak);
        tvRecentActivity = findViewById(R.id.tvRecentActivity);
        tvParentTip = findViewById(R.id.tvParentTip);
    }

    private void loadChildInfo() {
        if (!ChildSessionManager.hasChild()) {
            tvChildInfo.setText("No child selected");
            return;
        }

        db.collection("users")
                .document(userId)
                .collection("children")
                .document(childId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name = doc.getString("name");
                        String age = doc.getString("age");

                        if (name == null || name.isEmpty()) {
                            name = "Child";
                        }

                        if (age == null || age.isEmpty()) {
                            tvChildInfo.setText(name);
                        } else {
                            tvChildInfo.setText(name + ", Age: " + age);
                        }
                    } else {
                        tvChildInfo.setText("Child profile not found");
                    }
                })
                .addOnFailureListener(e -> {
                    tvChildInfo.setText("Unable to load child info");
                    Toast.makeText(this, "Failed to load child info", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadDashboardOverview(){

        if(!ChildSessionManager.hasChild()){
            setDefaultDashboard();
            return;
        }
        db.collection("users")
                .document(userId)
                .collection("children")
                .document(childId)
                .get()
                .addOnSuccessListener(doc -> {
                    if(doc.exists()){
                        int phonics =
                                getInt(doc,"phonicsScore",0);
                        int speech =
                                getInt(doc,"speechScore",0);
                        int quiz =
                                getInt(doc,"quizScore",0);
                        int averageScore =
                                (phonics + speech + quiz) / 3;
                        int completedActivities = 0;
                        if(phonics > 0){
                            completedActivities++;
                        }
                        if(speech > 0){
                            completedActivities++;
                        }
                        if(quiz > 0){
                            completedActivities++;
                        }
                        progressLearning.setProgress(
                                averageScore
                        );
                        tvProgressText.setText(
                                averageScore + "% completed"
                        );
                        tvCompletedActivities.setText(
                                String.valueOf(completedActivities)
                        );
                        tvAverageScore.setText(
                                averageScore + "%"
                        );
                        tvPracticeStreak.setText(
                                "0"
                        );
                        tvRecentActivity.setText(
                                "Completed Phonics, Speech and Quiz activities"
                        );
                        tvTodayGoal.setText(
                                "Continue practising reading and pronunciation."
                        );
                        tvParentTip.setText(
                                "Praise every improvement to build confidence."
                        );
                    }
                    else{
                        setDefaultDashboard();
                    }
                })
                .addOnFailureListener(e -> {
                    setDefaultDashboard();
                    Toast.makeText(
                            this,
                            "Failed load dashboard",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void setDefaultDashboard() {
        progressLearning.setProgress(0);
        tvProgressText.setText("0% completed");

        tvTodayGoal.setText("Practise reading for 10 minutes today.");
        tvCompletedActivities.setText("0");
        tvAverageScore.setText("0%");
        tvPracticeStreak.setText("0");

        tvRecentActivity.setText("No activity recorded yet.");
        tvParentTip.setText("Give praise after each small achievement to help build confidence.");
    }

    private int getInt(DocumentSnapshot doc, String fieldName, int defaultValue) {
        Long value = doc.getLong(fieldName);
        if (value == null) {
            return defaultValue;
        }
        return value.intValue();
    }
}