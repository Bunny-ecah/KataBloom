package com.example.myproject.viewmodel;

import com.example.myproject.ai.ReviewScheduler;
import com.example.myproject.data.SpacedRepository;

import java.util.List;
import java.util.Map;

public class SpacedController {

    SpacedRepository repo = new SpacedRepository();

    public interface UI {
        void showNext(String lesson, boolean urgent);
    }

    public void load(String userId, String childId, UI ui) {

        repo.load(userId, childId, (schedule, performance) -> {

            List<ReviewScheduler.Task> tasks =
                    ReviewScheduler.getTodayTasks(schedule, performance);

            ReviewScheduler.Task best = null;

            for (ReviewScheduler.Task t : tasks) {
                if (best == null || t.urgent) {
                    best = t;
                }
            }

            if (best == null) return;

            ui.showNext(best.lesson, best.urgent);
        });
    }
}