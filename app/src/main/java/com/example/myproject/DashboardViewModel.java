package com.example.myproject.viewmodel;

import com.example.myproject.data.ChildRepository;

public class DashboardViewModel {

    private final ChildRepository repo = new ChildRepository();

    public interface UI {
        void showData(String name, String age, int streak);
    }

    public void loadDashboard(String userId, String childId, UI ui) {

        repo.getChild(userId, childId, (name, age, streak) -> {

            ui.showData(name, age, streak);

        });
    }
}