package com.example.myproject.ai;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SpacedRepetitionAI {

    public static class Item {
        public String lessonId;
        public int intervalDays;
        public long nextReviewTime;
    }

    public static Item compute(
            String lessonId,
            double successRate,
            int currentInterval
    ) {

        Item item = new Item();
        item.lessonId = lessonId;

        // 🔥 RULE 1: LOW PERFORMANCE → SHORT INTERVAL
        if (successRate < 0.4) {
            item.intervalDays = 1;
        }

        // 🔥 RULE 2: MEDIUM PERFORMANCE → MEDIUM INTERVAL
        else if (successRate < 0.7) {
            item.intervalDays = Math.max(2, currentInterval);
        }

        // 🔥 RULE 3: HIGH PERFORMANCE → INCREASE INTERVAL
        else {
            item.intervalDays = currentInterval * 2;
        }

        // LIMIT MAX INTERVAL (avoid forgetting)
        if (item.intervalDays > 14) {
            item.intervalDays = 14;
        }

        item.nextReviewTime = addDays(item.intervalDays);

        return item;
    }

    private static long addDays(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, days);
        return c.getTimeInMillis();
    }
}