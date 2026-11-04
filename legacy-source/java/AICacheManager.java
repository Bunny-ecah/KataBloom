package com.example.myproject.cache;

import android.content.Context;
import android.content.SharedPreferences;

public class AICacheManager {

    private static final String PREF = "ai_cache";

    public static void save(Context c, String key, String value) {
        SharedPreferences sp = c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    public static String get(Context c, String key) {
        SharedPreferences sp = c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sp.getString(key, null);
    }
}