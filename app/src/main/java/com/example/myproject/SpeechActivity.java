package com.example.myproject;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SpeechActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SPEECH = 1001;
    // =========================
    // UI COMPONENT
    // =========================
    private TextView btnBack;
    private TextView tvWord;
    private TextView tvHint;
    private TextView tvFeedback;
    private TextView tvProgress;
    private TextView tvScore;
    private TextView tvBadge;
    private ImageView btnMic;
    private ImageView btnNext;
    private ImageView btnSpeaker;
    private ProgressBar progressBar;
    // =========================
    // FIREBASE
    // =========================
    private FirebaseFirestore db;
    // =========================
    // TEXT TO SPEECH
    // =========================
    private TextToSpeech textToSpeech;
    // =========================
    // DATA
    // =========================
    private List<SpeechItem> questions = new ArrayList<>();
    private int currentIndex = 0;
    private int score = 0;
    private boolean wordScored = false;
    private String userId;
    private String childId;
    private String level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
        db = FirebaseFirestore.getInstance();
        // Initialise TTS
        initTextToSpeech();
        userId = ChildSessionManager.userId;
        childId = getIntent()
                .getStringExtra("childId");
        level = getIntent()
                .getStringExtra("level");
        if(childId == null || childId.isEmpty()){
            childId = ChildSessionManager.childId;
        }
        if(level == null || level.isEmpty()){
            level = "easy";
        }
        if(childId == null || childId.isEmpty()){
            Toast.makeText(
                    this,
                    "Please select a child first",
                    Toast.LENGTH_SHORT
            ).show();
            finish();
            return;
        }
        initViews();
        setupClicks();
        loadQuestions();
    }

    // =========================
    // CONNECT XML COMPONENT
    // =========================
    private void initViews(){
        btnBack = findViewById(R.id.btnBack);
        tvWord = findViewById(R.id.tvWord);
        tvHint = findViewById(R.id.tvHint);
        tvFeedback = findViewById(R.id.tvFeedback);
        tvProgress = findViewById(R.id.tvProgress);
        tvScore = findViewById(R.id.tvScore);
        tvBadge = findViewById(R.id.tvBadge);
        btnMic = findViewById(R.id.btnMic);
        btnNext = findViewById(R.id.btnNext);
        btnSpeaker = findViewById(R.id.btnSpeaker);
        progressBar = findViewById(R.id.progressBar);
        btnMic.setEnabled(false);
        btnNext.setEnabled(false);
        tvWord.setText("Loading...");
        tvFeedback.setText(
                "Please wait..."
        );
        tvBadge.setText(
                "🏆 Badge: Start Learning!"
        );
    }

    // ========================
    // BUTTON ACTION
    // ========================
    private void setupClicks(){
        btnBack.setOnClickListener(v -> finish());
        // Student speaks
        btnMic.setOnClickListener(
                v -> startSpeechRecognition()
        );
        // Next question
        btnNext.setOnClickListener(
                v -> moveNext()
        );
        // Listen pronunciation
        btnSpeaker.setOnClickListener(
                v -> speakWord()
        );
    }

    // =========================
    // TEXT TO SPEECH
    // =========================
    private void initTextToSpeech(){
        textToSpeech = new TextToSpeech(
                this,
                status -> {
                    if(status == TextToSpeech.SUCCESS){
                        int result =
                                textToSpeech.setLanguage(
                                        Locale.US
                                );
                        if(result ==
                                TextToSpeech.LANG_NOT_SUPPORTED){
                            Toast.makeText(
                                    this,
                                    "Language not supported",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                }
        );
    }
    private void speakWord(){
        if(textToSpeech != null
                && !questions.isEmpty()){
            String word =
                    questions
                            .get(currentIndex)
                            .word;
            textToSpeech.speak(
                    word,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "speech_word"
            );
        }
    }

    // =========================
    // LOAD QUESTIONS FIREBASE
    // =========================
    private void loadQuestions(){
        db.collection("speech_questions")
                .whereEqualTo(
                        "level",
                        level
                )
                .get()
                .addOnSuccessListener(snapshot -> {
                    questions.clear();
                    for(QueryDocumentSnapshot doc : snapshot){
                        SpeechItem item =
                                new SpeechItem();
                        item.word =
                                getSafeText(
                                        doc.getString("word"),
                                        ""
                                );
                        item.hint =
                                getSafeText(
                                        doc.getString("hint"),
                                        "Say the word clearly"
                                );
                        item.correctAnswer =
                                getSafeText(
                                        doc.getString("expectedAnswer"),
                                        item.word
                                );
                        if(!item.word.isEmpty()){
                            questions.add(item);
                        }
                    }
                    if(questions.isEmpty()){
                        tvWord.setText("-");
                        tvFeedback.setText(
                                "No questions found"
                        );
                        btnMic.setEnabled(false);
                        btnNext.setEnabled(false);
                    }
                    else{
                        progressBar.setMax(
                                questions.size()
                        );
                        btnMic.setEnabled(true);
                        btnNext.setEnabled(true);
                        currentIndex = 0;
                        score = 0;
                        displayQuestion();
                    }
                })
                .addOnFailureListener(e -> {
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
        wordScored = false;
        SpeechItem item =
                questions.get(currentIndex);
        tvWord.setText(
                item.word
        );
        tvHint.setText(
                "Hint: " + item.hint
        );
        tvFeedback.setText(
                "Listen and pronounce the word"
        );
        progressBar.setProgress(
                currentIndex + 1
        );
        tvProgress.setText(
                "Progress: "
                        + (currentIndex + 1)
                        + "/"
                        + questions.size()
        );
        tvScore.setText(
                "⭐ Score: "
                        + score
        );
        updateBadge();
        // Automatically pronounce word
        speakWord();
    }

    // =========================
    // SPEECH RECOGNITION
    // =========================
    private void startSpeechRecognition(){
        if(questions.isEmpty()){
            return;
        }
        if(!SpeechRecognizer.isRecognitionAvailable(this)){
            Toast.makeText(
                    this,
                    "Speech recognition unavailable",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        Intent intent =
                new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH
                );
        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.US
        );
        intent.putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "Say: "
                        + questions
                        .get(currentIndex)
                        .word
        );
        startActivityForResult(
                intent,
                REQUEST_CODE_SPEECH
        );
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            @Nullable Intent data){
        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );
        if(requestCode == REQUEST_CODE_SPEECH
                && resultCode == RESULT_OK
                && data != null){
            ArrayList<String> result =
                    data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS
                    );
            if(result != null && !result.isEmpty()){
                String spoken =
                        result.get(0)
                                .toLowerCase()
                                .trim();
                checkAnswer(spoken);
            }
        }
    }

    // =========================
    // CHECK ANSWER
    // =========================
    private void checkAnswer(String spoken){
        if (questions.isEmpty() || currentIndex >= questions.size() || wordScored) return;
        SpeechItem current =
                questions.get(currentIndex);
        String answer =
                current.correctAnswer
                        .toLowerCase()
                        .trim();
        if(spoken.equals(answer)){
            wordScored = true;
            score++;
            tvFeedback.setText(
                    "🎉 Correct!\n"
                            + "You said: "
                            + spoken
            );
            Toast.makeText(
                    this,
                    "Great job! ⭐",
                    Toast.LENGTH_SHORT
            ).show();
        }
        else{
            tvFeedback.setText(
                    "💡 Try again\n"
                            + "You said: "
                            + spoken
                            + "\nCorrect answer: "
                            + answer
            );
        }
        tvScore.setText(
                "⭐ Score: "
                        + score
        );
        updateBadge();
    }

    // =========================
    // BADGE SYSTEM
    // =========================
    private void updateBadge(){
        if(score >= 10){
            tvBadge.setText(
                    "👑 Badge: Word Collector!"
            );
        }
        else if(score >= 8){
            tvBadge.setText(
                    "🏆 Badge: Speech Champion!"
            );
        }
        else if(score >= 5){
            tvBadge.setText(
                    "⭐ Badge: Word Explorer!"
            );
        }
        else if(score >= 3){
            tvBadge.setText(
                    "🌱 Badge: Beginner Reader!"
            );
        }
        else{
            tvBadge.setText(
                    "💪 Badge: Keep Practicing!"
            );
        }
    }

    // ========================
    // NEXT QUESTION
    // ========================
    private void moveNext(){
        if(currentIndex < questions.size() - 1){
            currentIndex++;
            displayQuestion();
        }
        else{
            saveSpeechResult();
            saveSpeechProgress();
            Toast.makeText(
                    this,
                    "🎉 Completed!\nFinal Score: "
                            + score
                            + "/"
                            + questions.size(),
                    Toast.LENGTH_LONG
            ).show();
            finish();
        }
    }

    // =========================
    // SAVE RESULT FIREBASE
    // =========================
    private void saveSpeechResult(){
        if(userId == null
                || userId.isEmpty()
                || childId == null
                || childId.isEmpty()){
            return;
        }
        Map<String,Object> result =
                new HashMap<>();
        result.put(
                "level",
                level
        );
        result.put(
                "score",
                score
        );
        result.put(
                "totalQuestions",
                questions.size()
        );
        result.put(
                "badge",
                getBadgeName()
        );
        result.put(
                "completedAt",
                FieldValue.serverTimestamp()
        );
        db.collection("users")
                .document(userId)
                .collection("children")
                .document(childId)
                .collection("speech_results")
                .add(result);
    }

    private void saveSpeechProgress(){

        if(userId == null
        ||userId.isEmpty()
        ||childId == null
        ||childId.isEmpty()){
            return;
        }
        if(questions == null ||  questions.isEmpty()){
            return;
        }
        int percentage =
                (score * 100) / questions.size();
        db.collection("users")
                .document(userId)
                .collection("children")
                .document(childId)
                .update(
                        "speechScore",
                        percentage
                );
    }
    private String getBadgeName(){
        if(score >= 10){
            return "Word Collector";
        }
        else if(score >= 8){
            return "Speech Champion";
        }
        else if(score >= 5){
            return "Word Explorer";
        }
        else if(score >= 3){
            return "Beginner Reader";
        }
        else{
            return "Keep Practicing";
        }
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
    // RELEASE TTS
    // =========================

    @Override
    protected void onDestroy(){
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
    // =========================
    // MODEL CLASS
    // =========================
    public static class SpeechItem{
        public String word;
        public String hint;
        public String correctAnswer;
    }
}
