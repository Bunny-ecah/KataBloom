package com.example.myproject.core;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class PhonicsEngine {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface PhonicsLoadCallback {
        void onLoaded(List<PhonicsItem> items);
        void onError(String error);
    }

    public void loadPhonics(String level, String childId, PhonicsLoadCallback callback) {
        db.collection("phonics_questions")
                .whereEqualTo("level", level)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<PhonicsItem> list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            PhonicsItem item = doc.toObject(PhonicsItem.class);
                            item.id = doc.getId();
                            list.add(item);
                        }
                        callback.onLoaded(list);
                    } else {
                        callback.onError(task.getException() != null ? task.getException().getMessage() : "Unknown error");
                    }
                });
    }
}