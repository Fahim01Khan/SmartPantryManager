package com.fahimkhan.smartpantry.database;

import static com.fahimkhan.smartpantry.database.PantryDBHelper.COL_RECIPE_ID;
import static com.fahimkhan.smartpantry.database.PantryDBHelper.COL_RECIPE_INSTRUCTIONS;
import static com.fahimkhan.smartpantry.database.PantryDBHelper.COL_RECIPE_NAME;
import static com.fahimkhan.smartpantry.database.PantryDBHelper.COL_RI_ID;
import static com.fahimkhan.smartpantry.database.PantryDBHelper.COL_RI_NAME;
import static com.fahimkhan.smartpantry.database.PantryDBHelper.COL_RI_QUANTITY;
import static com.fahimkhan.smartpantry.database.PantryDBHelper.COL_RI_RECIPE_ID;
import static com.fahimkhan.smartpantry.database.PantryDBHelper.COL_RI_UNIT;
import static com.fahimkhan.smartpantry.database.PantryDBHelper.TABLE_RECIPES;
import static com.fahimkhan.smartpantry.database.PantryDBHelper.TABLE_RECIPE_INGREDIENTS;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.fahimkhan.smartpantry.models.Recipe;
import com.fahimkhan.smartpantry.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Read-only access to the seeded recipes. Recipes are returned with their
 * ingredient lists already attached, ready for the matcher or a detail screen.
 */
public class RecipeDataSource {

    private final PantryDBHelper dbHelper;
    private SQLiteDatabase database;

    public RecipeDataSource(Context context) {
        dbHelper = new PantryDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getReadableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Returns every recipe, alphabetically, each with its ingredients.
     * Uses two queries in total (one for recipes, one for all ingredients)
     * rather than one ingredient query per recipe.
     */
    public List<Recipe> getAllRecipesWithIngredients() {
        // LinkedHashMap keeps the alphabetical order from the first query
        Map<Integer, Recipe> recipesById = new LinkedHashMap<>();
        try (Cursor cursor = database.query(TABLE_RECIPES, null, null, null,
                null, null, COL_RECIPE_NAME + " COLLATE NOCASE ASC")) {
            while (cursor.moveToNext()) {
                Recipe recipe = cursorToRecipe(cursor);
                recipesById.put(recipe.getId(), recipe);
            }
        }

        try (Cursor cursor = database.query(TABLE_RECIPE_INGREDIENTS, null, null, null,
                null, null, COL_RI_ID + " ASC")) {
            while (cursor.moveToNext()) {
                RecipeIngredient ingredient = cursorToIngredient(cursor);
                Recipe owner = recipesById.get(ingredient.getRecipeId());
                if (owner != null) {
                    owner.addIngredient(ingredient);
                }
            }
        }
        return new ArrayList<>(recipesById.values());
    }

    /**
     * Returns one recipe with its ingredients, or null if no recipe has that id.
     */
    public Recipe getRecipeById(int recipeId) {
        String[] args = {String.valueOf(recipeId)};
        Recipe recipe = null;

        try (Cursor cursor = database.query(TABLE_RECIPES, null,
                COL_RECIPE_ID + " = ?", args, null, null, null)) {
            if (cursor.moveToFirst()) {
                recipe = cursorToRecipe(cursor);
            }
        }
        if (recipe == null) {
            return null;
        }

        try (Cursor cursor = database.query(TABLE_RECIPE_INGREDIENTS, null,
                COL_RI_RECIPE_ID + " = ?", args, null, null, COL_RI_ID + " ASC")) {
            while (cursor.moveToNext()) {
                recipe.addIngredient(cursorToIngredient(cursor));
            }
        }
        return recipe;
    }

    private Recipe cursorToRecipe(Cursor cursor) {
        return new Recipe(
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECIPE_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_INSTRUCTIONS)));
    }

    private RecipeIngredient cursorToIngredient(Cursor cursor) {
        return new RecipeIngredient(
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_RI_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_RI_RECIPE_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_RI_NAME)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COL_RI_QUANTITY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_RI_UNIT)));
    }
}
