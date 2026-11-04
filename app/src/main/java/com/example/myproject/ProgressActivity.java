package com.example.myproject;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class ProgressActivity extends BaseActivity {

    // UI
    private TextView tvChildName, tvWeakness, tvBadge;
    private TextView tvPhonics, tvQuiz, tvSpeech;
    private ProgressBar pbPhonics, pbQuiz, pbSpeech;

    // Firestore
    private FirebaseFirestore db;
    private ListenerRegistration listener;

    private String userId;
    private String childId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        // Initialize UI
        tvChildName = findViewById(R.id.tvChildName);
        tvBadge = findViewById(R.id.tvBadge);

        tvPhonics = findViewById(R.id.tvPhonics);
        tvQuiz = findViewById(R.id.tvQuiz);
        tvSpeech = findViewById(R.id.tvSpeech);

        pbPhonics = findViewById(R.id.pbPhonics);
        pbQuiz = findViewById(R.id.pbQuiz);
        pbSpeech = findViewById(R.id.pbSpeech);

        db = FirebaseFirestore.getInstance();

        loadSession();
        startListening();
    }

    // Load current child session safely
    private void loadSession() {
        if (!ChildSessionManager.hasChild()) {
            tvChildName.setText("No child selected");
            return;
        }

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        childId = ChildSessionManager.childId;

        tvChildName.setText(ChildSessionManager.childName);
    }

    // Listen Firestore changes
    private void startListening() {
        if (userId == null || childId == null) return;

        DocumentReference ref = db.collection("users")
                .document(userId)
                .collection("children")
                .document(childId);

        listener = ref.addSnapshotListener((snapshot, error) -> {
            if (snapshot == null || !snapshot.exists()) return;

            int phonics = getInt(snapshot.getLong("phonicsScore"));
            int quiz = getInt(snapshot.getLong("quizScore"));
            int speech = getInt(snapshot.getLong("speechScore"));

            // Update progress bars
            pbPhonics.setProgress(phonics);
            pbQuiz.setProgress(quiz);
            pbSpeech.setProgress(speech);

            tvPhonics.setText(phonics + "%");
            tvQuiz.setText(quiz + "%");
            tvSpeech.setText(speech + "%");

            // Weakness and badge calculation
            String badge = BadgeEngine.getBadge(phonics, quiz, speech);

            tvBadge.setText("Badge: " + badge);
        });
    }

    private int getInt(Long value) {
        return value != null ? value.intValue() : 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener != null) listener.remove();
    }
}