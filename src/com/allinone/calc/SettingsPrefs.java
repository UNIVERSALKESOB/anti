package com.allinone.calc;

import android.content.Context;
import android.content.SharedPreferences;

public final class SettingsPrefs {
    private static final String PREFS = "app_settings";
    private static final String KEY_DECIMALS = "decimals"; // -1 means auto
    private static final String KEY_VIBRATE = "vibrate";
    private static final String KEY_COMMA_GROUP = "comma_group";

    private SettingsPrefs() {}

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    // Number of decimal places for formatting. -1 = auto (default behavior)
    public static int getDecimalPlaces(Context c) {
        return prefs(c).getInt(KEY_DECIMALS, -1);
    }

    public static void setDecimalPlaces(Context c, int v) {
        prefs(c).edit().putInt(KEY_DECIMALS, v).apply();
    }

    // Haptic/vibration feedback (reserved for future use)
    public static boolean isVibrate(Context c) {
        return prefs(c).getBoolean(KEY_VIBRATE, true);
    }

    public static void setVibrate(Context c, boolean v) {
        prefs(c).edit().putBoolean(KEY_VIBRATE, v).apply();
    }

    // Show thousand grouping commas
    public static boolean isCommaGroup(Context c) {
        return prefs(c).getBoolean(KEY_COMMA_GROUP, true);
    }

    public static void setCommaGroup(Context c, boolean v) {
        prefs(c).edit().putBoolean(KEY_COMMA_GROUP, v).apply();
    }

    public static void resetAll(Context c) {
        prefs(c).edit().clear().apply();
    }
}
