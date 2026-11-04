package com.example.myproject;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PhonicsManager {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface Callback {
        void onResult(List<PhonicsModel> list);
    }

    public void loadPhonics(String userId, String childId, Callback callback) {

        db.collection("users")
                .document(userId)
                .collection("children")
                .document(childId)
                .collection("phonics")
                .get()
                .addOnSuccessListener(snapshot -> {

                    List<PhonicsModel> list = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : snapshot) {

                        PhonicsModel model = new PhonicsModel(
                                doc.getString("letter"),
                                doc.getString("sound"),
                                doc.getString("word"),
                                Boolean.TRUE.equals(doc.getBoolean("completed"))
                        );

                        list.add(model);
                    }

                    callback.onResult(list);
                });
    }
}