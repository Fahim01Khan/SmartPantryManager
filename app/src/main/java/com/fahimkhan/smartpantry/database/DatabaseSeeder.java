package com.fahimkhan.smartpantry.database;

import static com.fahimkhan.smartpantry.database.PantryDBHelper.*;
import static com.fahimkhan.smartpantry.utils.Units.*;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.fahimkhan.smartpantry.models.RecipeIngredient;

/**
 * Inserts the built-in recipe collection. Called once, from
 * PantryDBHelper.onCreate, so recipes are never duplicated.
 * Design rules: ingredient names are lowercase and singular; each ingredient
 * always uses the same kind of unit chosen to match how home cooks measure it
 * (fresh produce such as potatoes and tomatoes is counted; dry goods are weighed;
 * liquids are measured by volume); salt, pepper and water are treated as staples
 * and are not listed as requirements.
 */
public final class DatabaseSeeder {

    private DatabaseSeeder() {
        // Static methods only
    }

    /** Inserts all recipes and their ingredients into the given database. */
    public static void seedRecipes(SQLiteDatabase db) {

        // ----- Egg dishes -----
        insertRecipe(db, "Scrambled Eggs",
                "1. Whisk the eggs and milk together.\n"
                        + "2. Melt the butter in a pan over low heat.\n"
                        + "3. Pour in the eggs and stir gently until just set.\n"
                        + "4. Season to taste and serve immediately.",
                ing("egg", 3, COUNT), ing("milk", 50, MILLILITRE), ing("butter", 10, GRAM));

        insertRecipe(db, "Boiled Eggs",
                "1. Place the eggs in a pot and cover with cold water.\n"
                        + "2. Bring to the boil, then simmer for 7 minutes for firm yolks.\n"
                        + "3. Cool the eggs in cold water, then peel and serve.",
                ing("egg", 2, COUNT));

        insertRecipe(db, "Cheese Omelette",
                "1. Whisk the eggs and season to taste.\n"
                        + "2. Melt the butter in a pan over medium heat.\n"
                        + "3. Pour in the eggs and cook until almost set.\n"
                        + "4. Sprinkle the grated cheese over one half, fold, and serve.",
                ing("egg", 3, COUNT), ing("cheese", 50, GRAM), ing("butter", 10, GRAM));

        insertRecipe(db, "Tomato Omelette",
                "1. Dice the tomato.\n"
                        + "2. Whisk the eggs and season to taste.\n"
                        + "3. Melt the butter, cook the tomato for 2 minutes, then add the eggs.\n"
                        + "4. Cook until set, fold, and serve.",
                ing("egg", 3, COUNT), ing("tomato", 1, COUNT), ing("butter", 10, GRAM));

        // ----- Bread dishes -----
        insertRecipe(db, "French Toast",
                "1. Whisk the eggs and milk in a shallow dish.\n"
                        + "2. Dip each slice of bread into the mixture on both sides.\n"
                        + "3. Fry in butter over medium heat until golden on both sides.\n"
                        + "4. Serve warm.",
                ing("bread", 4, COUNT), ing("egg", 2, COUNT),
                ing("milk", 100, MILLILITRE), ing("butter", 20, GRAM));

        insertRecipe(db, "Cheese Sandwich",
                "1. Butter the slices of bread.\n"
                        + "2. Slice the cheese and layer it on one slice.\n"
                        + "3. Top with the second slice, cut in half, and serve.",
                ing("bread", 2, COUNT), ing("cheese", 50, GRAM), ing("butter", 10, GRAM));

        insertRecipe(db, "Tomato Sandwich",
                "1. Butter the slices of bread.\n"
                        + "2. Slice the tomato and layer it on one slice.\n"
                        + "3. Season to taste, top with the second slice, and serve.",
                ing("bread", 2, COUNT), ing("tomato", 1, COUNT), ing("butter", 10, GRAM));

        insertRecipe(db, "Grilled Cheese",
                "1. Butter the outside of each slice of bread.\n"
                        + "2. Place the cheese between the slices, buttered sides out.\n"
                        + "3. Toast in a pan over medium heat for 3 minutes per side.\n"
                        + "4. Serve once golden and the cheese has melted.",
                ing("bread", 2, COUNT), ing("cheese", 75, GRAM), ing("butter", 20, GRAM));

        insertRecipe(db, "Egg Sandwich",
                "1. Boil the eggs for 9 minutes, cool, peel and mash them.\n"
                        + "2. Season to taste.\n"
                        + "3. Butter the bread, spread the egg on one slice, and close the sandwich.",
                ing("bread", 2, COUNT), ing("egg", 2, COUNT), ing("butter", 10, GRAM));

        // ----- Pasta and rice -----
        insertRecipe(db, "Garlic Pasta",
                "1. Cook the pasta in salted boiling water until tender, then drain.\n"
                        + "2. Gently fry the sliced garlic in the oil until fragrant, not brown.\n"
                        + "3. Toss the pasta in the garlic oil, season, and serve.",
                ing("pasta", 200, GRAM), ing("garlic", 3, COUNT), ing("oil", 30, MILLILITRE));

        insertRecipe(db, "Tomato Pasta",
                "1. Cook the pasta in salted boiling water, then drain.\n"
                        + "2. Fry the chopped onion and garlic in the oil until soft.\n"
                        + "3. Add the chopped tomatoes and simmer for 10 minutes.\n"
                        + "4. Stir in the pasta, season to taste, and serve.",
                ing("pasta", 200, GRAM), ing("tomato", 3, COUNT), ing("onion", 1, COUNT),
                ing("garlic", 2, COUNT), ing("oil", 30, MILLILITRE));

        insertRecipe(db, "Macaroni and Cheese",
                "1. Cook the pasta, then drain.\n"
                        + "2. Melt the butter, stir in the flour, and cook for 1 minute.\n"
                        + "3. Gradually whisk in the milk until the sauce thickens.\n"
                        + "4. Stir in the grated cheese, then mix with the pasta and serve.",
                ing("pasta", 200, GRAM), ing("cheese", 100, GRAM), ing("milk", 250, MILLILITRE),
                ing("butter", 25, GRAM), ing("flour", 25, GRAM));

        insertRecipe(db, "Egg Fried Rice",
                "1. Cook the rice and let it cool (day-old rice works best).\n"
                        + "2. Fry the chopped onion in the oil until soft.\n"
                        + "3. Add the rice and stir-fry for 3 minutes.\n"
                        + "4. Push the rice aside, scramble the eggs, then mix everything together.",
                ing("rice", 200, GRAM), ing("egg", 2, COUNT),
                ing("onion", 1, COUNT), ing("oil", 30, MILLILITRE));

        insertRecipe(db, "Vegetable Fried Rice",
                "1. Cook the rice and let it cool.\n"
                        + "2. Dice the carrot and onion, then stir-fry them in the oil for 5 minutes.\n"
                        + "3. Add the rice and stir-fry until hot.\n"
                        + "4. Season to taste and serve.",
                ing("rice", 200, GRAM), ing("carrot", 1, COUNT),
                ing("onion", 1, COUNT), ing("oil", 30, MILLILITRE));

        // ----- Potatoes (counted, because recipes and home cooks count them) -----
        insertRecipe(db, "Mashed Potatoes",
                "1. Peel and quarter the potatoes, then boil until soft (about 20 minutes).\n"
                        + "2. Drain, then mash with the butter.\n"
                        + "3. Beat in the warm milk until smooth, season to taste, and serve.",
                ing("potato", 4, COUNT), ing("butter", 50, GRAM), ing("milk", 100, MILLILITRE));

        insertRecipe(db, "Potato Wedges",
                "1. Preheat the oven to 200 °C.\n"
                        + "2. Cut the potatoes into wedges and toss with the oil and seasoning.\n"
                        + "3. Spread on a tray and bake for 35 to 40 minutes, turning halfway.",
                ing("potato", 3, COUNT), ing("oil", 30, MILLILITRE));

        insertRecipe(db, "Fried Potatoes and Onions",
                "1. Slice the potatoes thinly and the onions into rings.\n"
                        + "2. Fry the potatoes in the oil over medium heat for 15 minutes.\n"
                        + "3. Add the onions and fry until both are golden, then season and serve.",
                ing("potato", 3, COUNT), ing("onion", 2, COUNT), ing("oil", 45, MILLILITRE));

        // ----- Soups, smoothies and sweet -----
        insertRecipe(db, "Tomato Soup",
                "1. Fry the chopped onion and garlic in the butter until soft.\n"
                        + "2. Add the chopped tomatoes and 250 ml of water.\n"
                        + "3. Simmer for 20 minutes, then blend until smooth.\n"
                        + "4. Season to taste and serve hot.",
                ing("tomato", 6, COUNT), ing("onion", 1, COUNT),
                ing("garlic", 2, COUNT), ing("butter", 20, GRAM));

        insertRecipe(db, "Banana Smoothie",
                "1. Peel and slice the bananas.\n"
                        + "2. Blend with the milk until smooth.\n"
                        + "3. Pour into a glass and serve chilled.",
                ing("banana", 2, COUNT), ing("milk", 250, MILLILITRE));

        insertRecipe(db, "Pancakes",
                "1. Whisk the flour, sugar, eggs and milk into a smooth batter.\n"
                        + "2. Melt a little butter in a pan over medium heat.\n"
                        + "3. Pour in small amounts of batter and cook until bubbles form, then flip.\n"
                        + "4. Repeat with the remaining batter and serve warm.",
                ing("flour", 200, GRAM), ing("milk", 300, MILLILITRE), ing("egg", 2, COUNT),
                ing("sugar", 25, GRAM), ing("butter", 20, GRAM));
    }

    /** Inserts one recipe, then one recipe_ingredients row per required ingredient. */
    private static void insertRecipe(SQLiteDatabase db, String name, String instructions,
                                     RecipeIngredient... ingredients) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(COL_RECIPE_NAME, name);
        recipeValues.put(COL_RECIPE_INSTRUCTIONS, instructions);
        long recipeId = db.insertOrThrow(TABLE_RECIPES, null, recipeValues);

        for (RecipeIngredient ingredient : ingredients) {
            ContentValues values = new ContentValues();
            values.put(COL_RI_RECIPE_ID, recipeId);
            values.put(COL_RI_NAME, ingredient.getName());
            values.put(COL_RI_QUANTITY, ingredient.getQuantity());
            values.put(COL_RI_UNIT, ingredient.getUnit());
            db.insertOrThrow(TABLE_RECIPE_INGREDIENTS, null, values);
        }
    }

    /** Short helper so each recipe's ingredient list stays readable. */
    private static RecipeIngredient ing(String name, double quantity, String unit) {
        return new RecipeIngredient(-1, -1, name, quantity, unit);
    }
}
