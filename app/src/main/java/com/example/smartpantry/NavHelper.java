package com.example.smartpantry;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


final class NavHelper {

    static final int HOME = 0;
    static final int PANTRY = 1;
    static final int RECIPES = 2;
    static final int RESCUE = 3;
    static final int SETTINGS = 4;

    private static final int[] ITEM_IDS = {
            R.id.navHome, R.id.navPantry, R.id.navRecipes, R.id.navRescue, R.id.navSettings
    };

    private static final int[] ICON_IDS = {
            R.id.navHomeIcon, R.id.navPantryIcon, R.id.navRecipesIcon,
            R.id.navRescueIcon, R.id.navSettingsIcon
    };

    private static final int[] LABEL_IDS = {
            R.id.navHomeLabel, R.id.navPantryLabel, R.id.navRecipesLabel,
            R.id.navRescueLabel, R.id.navSettingsLabel
    };

    private static final Class<?>[] TARGETS = {
            MainActivity.class, PantryActivity.class, RecipeActivity.class,
            RescueActivity.class, SettingsActivity.class
    };

    private NavHelper() {
        // utility class - no instances
    }

    /** Highlights the current tab and makes every tab open its Activity with an Intent. */
    static void setupBottomNav(Activity activity, int selected) {

        for (int i = 0; i < ITEM_IDS.length; i++) {

            final int index = i;

            boolean isSelected = (i == selected);

            ((ImageView) activity.findViewById(ICON_IDS[i])).setSelected(isSelected);
            ((TextView) activity.findViewById(LABEL_IDS[i])).setSelected(isSelected);

            activity.findViewById(ITEM_IDS[i]).setOnClickListener(
                    v -> openTab(activity, index, selected));
        }
    }

    private static void openTab(Activity activity, int target, int current) {

        if (target == current) {
            return;
        }

        Intent intent = new Intent(activity, TARGETS[target]);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        if (target == HOME) {
            // go back to the existing Home screen and drop everything above it
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }

        activity.startActivity(intent);

        // tabs replace each other, so pressing Back from any tab returns to Home
        if (current != HOME) {
            activity.finish();
        }
    }

    /** Wires the back arrow and sets the header title on detail screens. */
    static void setupBackHeader(Activity activity, int titleRes) {

        ((TextView) activity.findViewById(R.id.txtHeaderTitle)).setText(titleRes);

        View back = activity.findViewById(R.id.btnBack);
        back.setOnClickListener(v -> activity.finish());
    }
}
