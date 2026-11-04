package com.example.myproject.viewmodel;

import android.content.Context;

import com.example.myproject.data.AIRepository;
import com.example.myproject.ai.SmartMemoryAIv2;
public class AIController {

    AIRepository repo = new AIRepository();

    public interface UI {
        void show(String skill, String focus, String difficulty, boolean cached);
    }

    public void load(Context c, String userId, String childId, UI ui) {

        repo.load(c, userId, childId, (memory, mastery, fromCache) -> {

            SmartMemoryAIv2.Result r =
                    SmartMemoryAIv2.compute(memory, mastery);

            ui.show(r.skill, r.focus, r.difficulty, fromCache);
        });
    }
}