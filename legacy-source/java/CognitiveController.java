package com.example.myproject.viewmodel;

import com.example.myproject.ai.CognitiveDyslexiaAI;
import com.example.myproject.data.CognitiveRepository;

import java.util.Map;

public class CognitiveController {

    CognitiveRepository repo = new CognitiveRepository();

    public interface UI {
        void show(String focus, String confusion, String difficulty, String load);
    }

    public void analyze(String userId, String childId, UI ui) {

        repo.load(userId, childId, (reading, response, fatigue, confusion) -> {

            CognitiveDyslexiaAI.CognitiveResult r =
                    CognitiveDyslexiaAI.analyze(reading, response, fatigue, confusion);

            ui.show(r.focusSkill, r.confusionType, r.difficulty, r.cognitiveLoad);
        });
    }
}