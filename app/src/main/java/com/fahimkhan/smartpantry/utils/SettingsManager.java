package com.fahimkhan.smartpantry.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Reads and writes the user's settings in SharedPreferences.
 * Every key and default value is defined here, so no other class
 * needs to know how settings are stored.
 */
public final class SettingsManager {

    private static final String PREFS_NAME = "SmartPantryPreferences";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts_enabled";
    private static final String KEY_EXPIRY_DAYS = "expiry_warning_days";
    private static final String KEY_DEFAULT_UNIT = "default_unit";

    public static final int MIN_EXPIRY_DAYS = 1;
    public static final int MAX_EXPIRY_DAYS = 7;
    private static final boolean DEFAULT_EXPIRY_ALERTS = true;
    private static final int DEFAULT_EXPIRY_DAYS = 3;
    private static final String NO_DEFAULT_UNIT = "";

    private SettingsManager() {
        // Static methods only
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ----- Expiry alerts -----

    public static boolean isExpiryAlertEnabled(Context context) {
        return prefs(context).getBoolean(KEY_EXPIRY_ALERTS, DEFAULT_EXPIRY_ALERTS);
    }

    public static void setExpiryAlertEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_EXPIRY_ALERTS, enabled).apply();
    }

    /** Days before expiry at which an item counts as "expiring soon", kept within 1–7. */
    public static int getExpiryWarningDays(Context context) {
        int days = prefs(context).getInt(KEY_EXPIRY_DAYS, DEFAULT_EXPIRY_DAYS);
        return Math.max(MIN_EXPIRY_DAYS, Math.min(MAX_EXPIRY_DAYS, days));
    }

    public static void setExpiryWarningDays(Context context, int days) {
        prefs(context).edit().putInt(KEY_EXPIRY_DAYS, days).apply();
    }

    // ----- Default unit -----

    /** The unit to pre-select for new ingredients, or "" for none. */
    public static String getDefaultUnit(Context context) {
        return prefs(context).getString(KEY_DEFAULT_UNIT, NO_DEFAULT_UNIT);
    }

    public static void setDefaultUnit(Context context, String unit) {
        prefs(context).edit().putString(KEY_DEFAULT_UNIT, unit).apply();
    }
}
