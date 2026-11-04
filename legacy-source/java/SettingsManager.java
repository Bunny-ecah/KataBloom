package com.example.myproject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsManager {

    private static final String LANGUAGE_KEY = "language";
    private static final String FONT_SIZE_KEY = "font_size";

    public static void setLanguage(Activity activity, String lang) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        prefs.edit().putString(LANGUAGE_KEY, lang).apply();
    }

    public static String getLanguage(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        return prefs.getString(LANGUAGE_KEY, "en"); // default English
    }

    public static void setFontSize(Activity activity, int size) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        prefs.edit().putInt(FONT_SIZE_KEY, size).apply();
    }

    public static int getFontSize(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        return prefs.getInt(FONT_SIZE_KEY, 16); // default 16sp
    }
}