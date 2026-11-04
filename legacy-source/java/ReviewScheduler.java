package com.example.myproject.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReviewScheduler {

    public static class Task {
        public String lesson;
        public long nextDue;
        public boolean urgent;
    }

    public static List<Task> getTodayTasks(
            Map<String, Long> schedule,
            Map<String, Double> performance
    ) {

        List<Task> tasks = new ArrayList<>();

        long now = System.currentTimeMillis();

        for (String lesson : schedule.keySet()) {

            long due = schedule.get(lesson);
            double score = com.example.myproject.MapValues.get(performance, lesson, 0.5);

            Task t = new Task();
            t.lesson = lesson;
            t.nextDue = due;

            // 🔥 URGENT if overdue OR low performance
            t.urgent = due <= now || score < 0.5;

            tasks.add(t);
        }

        return tasks;
    }
}