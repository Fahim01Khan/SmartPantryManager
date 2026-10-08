package com.fahimkhan.smartpantry.utils;

import com.fahimkhan.smartpantry.models.PantryItem;
import com.fahimkhan.smartpantry.models.Recipe;
import com.fahimkhan.smartpantry.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Applies the strict-matching rule: a recipe qualifies only if EVERY required
 * ingredient is in the pantry, in at least the required quantity, measured in
 * a compatible unit. Partial matches are never returned.
 */
public final class RecipeMatcher {

    /**
     * Allowance for floating-point rounding. Decimal values are not always exact
     * in binary (0.1 + 0.2 = 0.30000000000000004), so an exact-equality check
     * could wrongly reject a pantry that has exactly enough.
     */
    private static final double TOLERANCE = 1e-6;

    private RecipeMatcher() {
        // Static methods only
    }

    /** Returns only the recipes the pantry can fully satisfy, in their original order. */
    public static List<Recipe> findMakeableRecipes(List<Recipe> recipes, List<PantryItem> pantry) {
        Map<String, List<PantryItem>> pantryByName = groupByNormalisedName(pantry);
        List<Recipe> makeable = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (canMake(recipe, pantryByName)) {
                makeable.add(recipe);
            }
        }
        return makeable;
    }

    /** A recipe qualifies only if every one of its ingredients passes. */
    private static boolean canMake(Recipe recipe, Map<String, List<PantryItem>> pantryByName) {
        if (recipe.getIngredients().isEmpty()) {
            return false;   // A recipe with no ingredients is a data error, never suggested
        }
        for (RecipeIngredient required : recipe.getIngredients()) {
            if (!hasEnough(required, pantryByName)) {
                return false;   // One failing ingredient rules the whole recipe out
            }
        }
        return true;
    }

    /**
     * Checks one required ingredient: is it in the pantry, in a compatible unit,
     * in at least the required amount? Compatible pantry entries are added together.
     */
    private static boolean hasEnough(RecipeIngredient required,
                                     Map<String, List<PantryItem>> pantryByName) {
        List<PantryItem> candidates =
                pantryByName.get(IngredientNormaliser.normalise(required.getName()));
        if (candidates == null) {
            return false;   // Ingredient is missing from the pantry
        }

        double requiredAmount =
                UnitConverter.toBaseQuantity(required.getQuantity(), required.getUnit());
        double availableAmount = 0;
        for (PantryItem item : candidates) {
            // Incompatible units (e.g. potatoes in kg vs a recipe counting potatoes) add nothing
            if (UnitConverter.areCompatible(item.getUnit(), required.getUnit())) {
                availableAmount += UnitConverter.toBaseQuantity(item.getQuantity(), item.getUnit());
            }
        }
        return availableAmount + TOLERANCE >= requiredAmount;
    }

    /** Indexes the pantry by canonical ingredient name for fast lookup. */
    private static Map<String, List<PantryItem>> groupByNormalisedName(List<PantryItem> pantry) {
        Map<String, List<PantryItem>> grouped = new HashMap<>();
        for (PantryItem item : pantry) {
            String key = IngredientNormaliser.normalise(item.getName());
            List<PantryItem> entries = grouped.get(key);
            if (entries == null) {
                entries = new ArrayList<>();
                grouped.put(key, entries);
            }
            entries.add(item);
        }
        return grouped;
    }
}
