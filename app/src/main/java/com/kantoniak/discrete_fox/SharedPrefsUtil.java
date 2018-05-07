package com.kantoniak.discrete_fox;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPrefsUtil - responsible for managing the application preferences
 */
public class SharedPrefsUtil {

    /**
     * Highscore preference
     */
    private static final String PREF_HIGHSCORE = "highscore";
    /**
     * Don't show rules preference
     */
    private static final String PREF_RULES_NOT_AGAIN = "rules_not_again";

    /**
     * Update the highscore in the preferences
     * @param context Context of the application
     * @param newScore New score acquired during the game
     * @return True if newScore is indeed the new high score
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

    /**
     * Read the flag for not showing the rules
     * @param context Context of the application
     * @return True if we should show the rules
     */
    public static boolean shouldShowRules(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        return !prefs.getBoolean(PREF_RULES_NOT_AGAIN, false);
    }

    /**
     * Set flag for not showing the rules
     * @param context Context of the application
     */
    public static void dontShowRulesAgain(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(PREF_RULES_NOT_AGAIN, true).apply();
    }

}
