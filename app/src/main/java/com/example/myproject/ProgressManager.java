package com.example.myproject;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class ProgressManager {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface Callback {
        void onResult(ProgressModel model);
    }

    public ListenerRegistration listenProgress(
            String userId,
            String childId,
            Callback callback
    ) {

        return db.collection("users")
                .document(userId)
                .collection("children")
                .document(childId)
                .addSnapshotListener((doc, error) -> {

                    if (doc == null || !doc.exists()) return;

                    ProgressModel model = new ProgressModel();

                    // ---------------- PHONICS ----------------
                    db.collection("users")
                            .document(userId)
                            .collection("children")
                            .document(childId)
                            .collection("phonics")
                            .get()
                            .addOnSuccessListener(p -> {

                                int done = 0;
                                int total = p.size();

                                for (var d : p) {
                                    Boolean c = d.getBoolean("completed");
                                    if (Boolean.TRUE.equals(c)) done++;
                                }

                                model.phonicsCompleted = done;
                                model.phonicsTotal = total;

                                // ---------------- QUIZ ----------------
                                db.collection("users")
                                        .document(userId)
                                        .collection("children")
                                        .document(childId)
                                        .collection("quiz")
                                        .get()
                                        .addOnSuccessListener(q -> {

                                            double sum = 0;
                                            int count = q.size();

                                            for (var d : q) {
                                                Double s = d.getDouble("score");
                                                if (s != null) sum += s;
                                            }

                                            model.quizScore = count == 0 ? 0 : sum / count;

                                            // ---------------- SPEECH ----------------
                                            db.collection("users")
                                                    .document(userId)
                                                    .collection("children")
                                                    .document(childId)
                                                    .collection("speech")
                                                    .get()
                                                    .addOnSuccessListener(s -> {

                                                        double sum2 = 0;
                                                        int c2 = s.size();

                                                        for (var d : s) {
                                                            Double a = d.getDouble("accuracy");
                                                            if (a != null) sum2 += a;
                                                        }

                                                        model.speechScore = c2 == 0 ? 0 : sum2 / c2;

                                                        callback.onResult(model);
                                                    });
                                        });
                            });
                });
    }
}