package com.fahimkhan.smartpantry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.fahimkhan.smartpantry.models.PantryItem;
import com.fahimkhan.smartpantry.models.Recipe;
import com.fahimkhan.smartpantry.models.RecipeIngredient;
import com.fahimkhan.smartpantry.utils.RecipeMatcher;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Verifies the strict-matching rule, including the examples from the assignment brief. */
public class RecipeMatcherTest {

    // ---------- Brief examples ----------

    @Test
    public void briefRecipeA_allIngredientsInStock_isSuggested() {
        Recipe recipeA = recipe("Recipe A",
                needs("egg", 2, "unit"), needs("milk", 250, "ml"), needs("flour", 200, "g"));
        assertTrue(canMake(recipeA, briefPantry()));
    }

    @Test
    public void briefRecipeB_missingButter_isNotSuggested() {
        Recipe recipeB = recipe("Recipe B",
                needs("egg", 2, "unit"), needs("milk", 250, "ml"),
                needs("flour", 200, "g"), needs("butter", 50, "g"));
        assertFalse(canMake(recipeB, briefPantry()));
    }

    // ---------- Quantity rules ----------

    @Test
    public void quantityTooLow_isNotSuggested() {
        Recipe r = recipe("Boiled Eggs", needs("egg", 2, "unit"));
        assertFalse(canMake(r, pantry("Eggs", 1, "unit")));
    }

    @Test
    public void exactQuantity_isSuggested() {
        Recipe r = recipe("Boiled Eggs", needs("egg", 2, "unit"));
        assertTrue(canMake(r, pantry("Eggs", 2, "unit")));
    }

    // ---------- Unit rules ----------

    @Test
    public void kilogramsInPantry_coverGramsInRecipe() {
        Recipe r = recipe("Pancake base", needs("flour", 500, "g"));
        assertTrue(canMake(r, pantry("Flour", 1, "kg")));
    }

    @Test
    public void litresInPantry_coverMillilitresInRecipe() {
        Recipe r = recipe("Smoothie base", needs("milk", 300, "ml"));
        assertTrue(canMake(r, pantry("Milk", 0.3, "L")));
    }

    @Test
    public void incompatibleUnits_isNotSuggested() {
        // Potatoes entered by weight cannot satisfy a recipe that counts potatoes
        Recipe r = recipe("Mashed Potatoes", needs("potato", 4, "unit"));
        assertFalse(canMake(r, pantry("Potatoes", 2, "kg")));
    }

    @Test
    public void compatibleEntries_areAddedTogether() {
        Recipe r = recipe("Milk drink", needs("milk", 250, "ml"));
        assertTrue(canMake(r,
                pantry("Milk", 200, "ml"),
                pantry("milk", 0.1, "L")));   // 200 ml + 100 ml = 300 ml
    }

    // ---------- Name handling ----------

    @Test
    public void pluralPantryName_matchesSingularRecipeName() {
        Recipe r = recipe("Tomato Sandwich", needs("tomato", 1, "unit"));
        assertTrue(canMake(r, pantry("Tomatoes", 3, "unit")));
    }

    @Test
    public void synonymPantryName_matchesRecipeName() {
        Recipe r = recipe("Pancake base", needs("flour", 200, "g"));
        assertTrue(canMake(r, pantry("Cake flour", 1, "kg")));
    }

    // ---------- Whole-list behaviour ----------

    @Test
    public void onlyQualifyingRecipes_areReturned() {
        Recipe boiled = recipe("Boiled Eggs", needs("egg", 2, "unit"));
        Recipe toast = recipe("French Toast",
                needs("bread", 4, "unit"), needs("egg", 2, "unit"));
        List<Recipe> result = RecipeMatcher.findMakeableRecipes(
                Arrays.asList(boiled, toast),
                Collections.singletonList(pantry("Eggs", 6, "unit")));

        assertEquals(1, result.size());
        assertEquals("Boiled Eggs", result.get(0).getName());
    }

    @Test
    public void emptyPantry_suggestsNothing() {
        Recipe r = recipe("Boiled Eggs", needs("egg", 2, "unit"));
        assertFalse(canMake(r));
    }

    // ---------- Test helpers ----------

    /** The pantry used in the assignment brief's worked example. */
    private static PantryItem[] briefPantry() {
        return new PantryItem[]{
                pantry("Eggs", 4, "unit"),
                pantry("Milk", 1, "L"),
                pantry("Flour", 500, "g"),
                pantry("Sugar", 200, "g")};
    }

    private static boolean canMake(Recipe recipe, PantryItem... items) {
        return !RecipeMatcher.findMakeableRecipes(
                Collections.singletonList(recipe), Arrays.asList(items)).isEmpty();
    }

    private static Recipe recipe(String name, RecipeIngredient... ingredients) {
        Recipe recipe = new Recipe(1, name, "Test instructions");
        for (RecipeIngredient ingredient : ingredients) {
            recipe.addIngredient(ingredient);
        }
        return recipe;
    }

    private static RecipeIngredient needs(String name, double quantity, String unit) {
        return new RecipeIngredient(-1, 1, name, quantity, unit);
    }

    private static PantryItem pantry(String name, double quantity, String unit) {
        return new PantryItem(-1, name, quantity, unit, null);
    }
}
