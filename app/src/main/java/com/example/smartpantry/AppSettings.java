package com.example.smartpantry;

import android.content.Context;
import android.content.SharedPreferences;

/** Small wrapper around SharedPreferences for the user's settings and impact counters. */
public final class AppSettings {

    private static final String PREFS_NAME = "SmartPantrySettings";
    private static final String KEY_EXPIRY_ALERTS = "expiryAlerts";
    private static final String KEY_MEALS_COOKED = "mealsCooked";
    private static final String KEY_INGREDIENTS_RESCUED = "ingredientsRescued";

    private AppSettings() {
        // utility class - no instances
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /** Whether expiring-soon alerts are switched on (default: on). */
    public static boolean isExpiryAlertsEnabled(Context context) {
        return prefs(context).getBoolean(KEY_EXPIRY_ALERTS, true);
    }

    public static void setExpiryAlertsEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_EXPIRY_ALERTS, enabled).apply();
    }

    /** Number of successful Cook & Deduct actions completed on this device. */
    public static int getMealsCooked(Context context) {
        return prefs(context).getInt(KEY_MEALS_COOKED, 0);
    }

    /** Number of soon-to-expire pantry rows actually used while cooking. */
    public static int getIngredientsRescued(Context context) {
        return prefs(context).getInt(KEY_INGREDIENTS_RESCUED, 0);
    }

    /** Records one cooked meal plus any expiring pantry rows that were used. */
    public static void recordCookedMeal(Context context, int rescuedIngredients) {
        SharedPreferences preferences = prefs(context);

        int meals = preferences.getInt(KEY_MEALS_COOKED, 0) + 1;
        int rescued = preferences.getInt(KEY_INGREDIENTS_RESCUED, 0)
                + Math.max(0, rescuedIngredients);

        preferences.edit()
                .putInt(KEY_MEALS_COOKED, meals)
                .putInt(KEY_INGREDIENTS_RESCUED, rescued)
                .apply();
    }
}
