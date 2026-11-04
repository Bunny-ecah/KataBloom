package com.example.myproject;


import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import com.example.myproject.core.PhonicsItem;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PhonicsActivity extends AppCompatActivity {
    // UI
    private TextView btnBack;
    private TextView tvBigLetter;
    private TextView tvSmallLetter;
    private TextView tvSound;
    private TextView tvWord;
    private TextView tvProgress;
    private ImageView btnSpeaker;
    private Button btnNext;
    // Firebase
    private FirebaseFirestore db;
    // TTS
    private TextToSpeech textToSpeech;
    // Data
    private List<PhonicsItem> questions = new ArrayList<>();
    private int currentIndex = 0;
    private String childId;
    private String level;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonics);
        db = FirebaseFirestore.getInstance();
        initTextToSpeech();
        childId = getIntent()
                .getStringExtra("childId");
        level = getIntent()
                .getStringExtra("level");
        if(childId == null || childId.isEmpty()){
            childId =
                    ChildSessionManager.childId;
        }
        if(level == null || level.isEmpty()){
            level = "easy";
        }
        initViews();
        setupClicks();
        loadQuestions();
    }
    // =========================
    // INITIALIZE VIEW
    // =========================
    private void initViews(){
        btnBack = findViewById(R.id.btnBack);
        tvBigLetter =
                findViewById(R.id.tvBigLetter);
        tvSmallLetter =
                findViewById(R.id.tvSmallLetter);
        tvSound =
                findViewById(R.id.tvSound);
        tvWord =
                findViewById(R.id.tvWord);
        tvProgress =
                findViewById(R.id.tvProgress);
        btnSpeaker =
                findViewById(R.id.btnSpeaker);
        btnNext =
                findViewById(R.id.btnNext);
        btnNext.setEnabled(false);
        btnNext.setText(
                "Loading..."
        );
    }
    // =========================
    // BUTTON CLICK
    // =========================
    private void setupClicks(){
        btnBack.setOnClickListener(v -> finish());
        btnNext.setOnClickListener(
                v -> showNext()
        );
        btnSpeaker.setOnClickListener(
                v -> speakLetter()
        );
    }
    // ========================
    // TEXT TO SPEECH
    // =========================
    private void initTextToSpeech(){
        textToSpeech = new TextToSpeech(
                this,
                status -> {
                    if(status == TextToSpeech.SUCCESS){
                        textToSpeech.setLanguage(
                                Locale.US
                        );
                    }
                }
        );
    }
    private void speakLetter(){
        if(textToSpeech != null && !questions.isEmpty()){
            PhonicsItem item =
                    questions.get(currentIndex);
            String sound =
                    item.audioText;
            String letter =
                    item.smallLetter;
            textToSpeech.speak(
                    sound,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "phonics_sound"
            );
        }
    }
    // =========================
    // LOAD FIREBASE QUESTIONS
    // =========================
    private void loadQuestions(){
        db.collection("phonics_questions")
                .whereEqualTo(
                        "level",
                        level
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    questions.clear();
                    for(QueryDocumentSnapshot doc : querySnapshot){
                        String bigLetter =
                                doc.getString("bigLetter");
                        String smallLetter =
                                doc.getString("smallLetter");
                        String sound =
                                doc.getString("sound");
                        String audioText =
                                doc.getString("audioText");
                        String word =
                                doc.getString("word");
                        String levelValue =
                                doc.getString("level");
                        PhonicsItem item =
                                new PhonicsItem(
                                        bigLetter,
                                        smallLetter,
                                        sound,
                                        audioText,
                                        word,
                                        levelValue
                                );
                        questions.add(item);
                    }
                    if(questions.isEmpty()){
                        tvBigLetter.setText("-");
                        tvSmallLetter.setText("-");
                        tvSound.setText("No sound");
                        tvWord.setText(
                                "No questions found"
                        );
                        tvProgress.setText(
                                "Progress: 0/0"
                        );
                        btnNext.setText(
                                "No Questions"
                        );
                        btnNext.setEnabled(false);
                        Toast.makeText(
                                this,
                                "No phonics questions found",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                    else{
                        currentIndex = 0;
                        btnNext.setEnabled(true);
                        displayQuestion();
                    }
                })
                .addOnFailureListener(e -> {
                    btnNext.setText(
                            "Try Again"
                    );
                    btnNext.setEnabled(true);
                    Toast.makeText(
                            this,
                            "Failed: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
    // =========================
    // DISPLAY QUESTION
    // =========================
    private void displayQuestion(){
        PhonicsItem item =
                questions.get(currentIndex);
        tvBigLetter.setText(
                getSafeText(
                        item.bigLetter,
                        "-"
                )
        );
        tvSmallLetter.setText(
                getSafeText(
                        item.smallLetter,
                        "-"
                )
        );
        tvSound.setText(
                getSafeText(
                        item.sound,
                        "No sound"
                )
        );
        tvWord.setText(
                getSafeText(
                        item.word,
                        "No word"
                )
        );
        tvProgress.setText(
                "Progress: "
                        + (currentIndex + 1)
                        + "/"
                        + questions.size()
        );
        if(currentIndex == questions.size() - 1) {
            btnNext.setText(
                    "Finish"
            );
        }
        else{
            btnNext.setText(
                    "Next Question"
            );
        }
        // Automatically pronounce word
        speakLetter();
    }
    // =========================
    // NEXT QUESTION
    // =========================
    private void showNext(){
        if(questions.isEmpty()){
            Toast.makeText(
                    this,
                    "No questions available",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        if(currentIndex < questions.size() - 1){
            currentIndex++;
            displayQuestion();
        }
        else{
            savePhonicsProgress();
            Toast.makeText(
                    this,
                    "🎉 Phonics completed!",
                    Toast.LENGTH_LONG
            ).show();
            finish();
        }
    }

    private void savePhonicsProgress(){

        if(childId == null){
            return;
        }


        int percentage =
                ((currentIndex + 1) * 100)
                        / questions.size();



        db.collection("users")
                .document(ChildSessionManager.userId)
                .collection("children")
                .document(childId)
                .update(
                        "phonicsScore",
                        percentage
                );

    }
    // =========================
    // SAFE TEXT
    // =========================
    private String getSafeText(
            String value,
            String defaultValue){
        if(value == null
                || value.trim().isEmpty()){
            return defaultValue;
        }
        return value;
    }
    // =========================
    // CLOSE TEXT TO SPEECH
    // =========================
    @Override
    protected void onDestroy(){
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}