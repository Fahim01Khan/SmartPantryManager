package com.fahimkhan.smartpantry.activities;

import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fahimkhan.smartpantry.R;
import com.fahimkhan.smartpantry.database.RecipeDataSource;
import com.fahimkhan.smartpantry.models.Recipe;
import com.fahimkhan.smartpantry.models.RecipeIngredient;
import com.fahimkhan.smartpantry.utils.FormatUtils;
import com.google.android.material.appbar.MaterialToolbar;

/**
 * Shows one recipe's full ingredient list and method.
 * Opened with EXTRA_RECIPE_ID; the recipe itself is loaded from the database.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    /**
     * Intent extra key: the id of the recipe to display.
     */
    public static final String EXTRA_RECIPE_ID = "com.fahimkhan.smartpantry.EXTRA_RECIPE_ID";

    private static final String TAG = "RecipeDetailActivity";
    private static final int NO_RECIPE = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);   // No nav bar here
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbarRecipe);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Recipes never change while the app runs, so loading once in onCreate is enough
        int recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, NO_RECIPE);
        Recipe recipe = loadRecipe(recipeId);
        if (recipe == null) {
            Toast.makeText(this, R.string.error_recipe_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        displayRecipe(recipe);
    }

    /**
     * Loads the recipe and its ingredients, or returns null if it can't be found.
     */
    private Recipe loadRecipe(int recipeId) {
        if (recipeId == NO_RECIPE) {
            return null;
        }
        RecipeDataSource dataSource = new RecipeDataSource(this);
        try {
            dataSource.open();
            return dataSource.getRecipeById(recipeId);
        } catch (SQLException e) {
            Log.e(TAG, "Failed to load recipe " + recipeId, e);
            return null;
        } finally {
            dataSource.close();
        }
    }

    /**
     * Fills the screen: name, one bulleted line per ingredient, then the method.
     */
    private void displayRecipe(Recipe recipe) {
        TextView textName = findViewById(R.id.textDetailName);
        TextView textIngredients = findViewById(R.id.textDetailIngredients);
        TextView textInstructions = findViewById(R.id.textDetailInstructions);

        textName.setText(recipe.getName());

        StringBuilder lines = new StringBuilder();
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            if (lines.length() > 0) {
                lines.append('\n');
            }
            lines.append("•  ").append(FormatUtils.formatIngredientLine(
                    ingredient.getName(), ingredient.getQuantity(), ingredient.getUnit()));
        }
        textIngredients.setText(lines.toString());
        textInstructions.setText(recipe.getInstructions());
    }
}