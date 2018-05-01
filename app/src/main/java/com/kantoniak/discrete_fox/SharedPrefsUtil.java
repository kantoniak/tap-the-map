package com.kantoniak.discrete_fox;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsUtil {

    private static final String PREF_HIGHSCORE = "highscore";
    private static final String PREF_RULES_NOT_AGAIN = "rules_not_again";

    /**
     * @return true if newScore is indeed the new high score
     */
    public static boolean updateHighScore(Context context, int newScore) {
        SharedPreferences prefs = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        int currentHighscore = prefs.getInt(PREF_HIGHSCORE, 0);
        if (newScore <= currentHighscore) {
            return false;
        }
        prefs.edit().putInt(PREF_HIGHSCORE, newScore).apply();
        return true;
    }

    public static boolean shouldShowRules(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        return !prefs.getBoolean(PREF_RULES_NOT_AGAIN, false);
    }

    public static void dontShowRulesAgain(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(PREF_RULES_NOT_AGAIN, true).apply();
    }

}
