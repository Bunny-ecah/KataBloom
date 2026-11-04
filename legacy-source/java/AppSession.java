package com.example.myproject;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSession {

    private static final String PREF_NAME = "app_session";

    // KEYS
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_CHILD_ID = "child_id";
    private static final String KEY_CHILD_NAME = "child_name";
    private static final String KEY_CHILD_AGE = "child_age";
    private static final String KEY_CHILD_IMAGE = "child_image";

    // ========================
    // USER SESSION
    // ========================
    public static void setUserId(Context context, String userId) {

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pref.edit().putString(KEY_USER_ID, userId).apply();
    }

    public static String getUserId(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_USER_ID, null);
    }

    // ========================
    // CHILD SESSION
    // ========================
    public static void setActiveChild(Context context,
                                      String userId,
                                      String childId,
                                      String childName,
                                      String childAge,
                                      String childImage) {

        SharedPreferences pref = context.getSharedPreferences("app_session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("user_id", userId);
        editor.putString("child_id", childId);
        editor.putString("child_name", childName);
        editor.putString("child_age", childAge);
        editor.putString("child_image", childImage);

        editor.apply();
    }

    // GET CHILD ID
    public static String getChildId(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_CHILD_ID, null);
    }

    // GET CHILD NAME
    public static String getChildName(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_CHILD_NAME, null);
    }

    // GET CHILD AGE
    public static String getChildAge(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_CHILD_AGE, null);
    }

    // GET CHILD IMAGE
    public static String getChildImage(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_CHILD_IMAGE, null);
    }

    // ========================
    // CLEAR ALL SESSION
    // ========================
    public static void clear(Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }
}