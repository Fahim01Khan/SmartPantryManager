package com.fahimkhan.smartpantry.utils;

import android.app.Activity;
import android.content.Intent;

import com.fahimkhan.smartpantry.R;
import com.fahimkhan.smartpantry.activities.PantryListActivity;
import com.fahimkhan.smartpantry.activities.SettingsActivity;
import com.fahimkhan.smartpantry.activities.SuggestedRecipesActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Sets up the shared bottom navigation bar so every main screen
 * behaves the same way, without repeating the code in each Activity.
 */
public final class NavigationHelper {

    private NavigationHelper() {
        // Static methods only
    }

    /**
     * Highlights the current screen and opens the chosen screen when another tab is tapped.
     *
     * @param currentItemId the menu id of the screen calling this method
     */
    public static void setUp(Activity activity, BottomNavigationView bottomNav, int currentItemId) {
        // Set the highlight BEFORE adding the listener, so it doesn't trigger navigation
        bottomNav.setSelectedItemId(currentItemId);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == currentItemId) {
                return true;   // Already on this screen
            }
            Class<?> target = targetFor(itemId);
            if (target == null) {
                return false;
            }
            Intent intent = new Intent(activity, target);
            // Reuse an existing copy of the screen instead of stacking duplicates
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);
            // Pantry is the home tab: leaving any other tab closes it, so Back from
            // Recipes or Settings always returns to Pantry, and Back from Pantry exits
            if (currentItemId != R.id.nav_pantry) {
                activity.finish();
            }
            // false: this screen's bar keeps highlighting itself; the new screen highlights its own tab
            return false;
        });
    }

    /**
     * Maps a menu item to the Activity it opens.
     */
    private static Class<?> targetFor(int itemId) {
        if (itemId == R.id.nav_pantry) {
            return PantryListActivity.class;
        }
        if (itemId == R.id.nav_recipes) {
            return SuggestedRecipesActivity.class;
        }
        if (itemId == R.id.nav_settings) {
            return SettingsActivity.class;
        }
        return null;
    }
}
