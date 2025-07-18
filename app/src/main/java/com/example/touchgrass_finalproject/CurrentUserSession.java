package com.example.touchgrass_finalproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

public class CurrentUserSession {

    public static String getUsername(Context context) {

        SharedPreferences prefs = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return prefs.getString("username", "unknown");

    }

    public static String getUserID(Context context) {

        SharedPreferences prefs = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return prefs.getString("uuid", "");

    }
}
