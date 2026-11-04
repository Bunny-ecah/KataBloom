package com.example.myproject.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSettingsManager {

    private static final String PREF_NAME = "app_settings";

    private SharedPreferences pref;

    public AppSettingsManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // ---------------- THEME ----------------
    public void setDarkMode(boolean enabled) {
        pref.edit().putBoolean("dark_mode", enabled).apply();
    }

    public boolean isDarkMode() {
        return pref.getBoolean("dark_mode", false);
    }

    // ---------------- LANGUAGE ----------------
    public void setMalayMode(boolean enabled) {
        pref.edit().putBoolean("bm_mode", enabled).apply();
    }

    public boolean isMalayMode() {
        return pref.getBoolean("bm_mode", false);
    }

    // ---------------- FONT SIZE ----------------
    public void setFontSize(int size) {
        pref.edit().putInt("font_size", size).apply();
    }

    public int getFontSize() {
        return pref.getInt("font_size", 16);
    }
}