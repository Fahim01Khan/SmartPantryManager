package com.fahimkhan.smartpantry.activities;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fahimkhan.smartpantry.R;
import com.fahimkhan.smartpantry.adapters.RecipeAdapter;
import com.fahimkhan.smartpantry.database.PantryDataSource;
import com.fahimkhan.smartpantry.database.RecipeDataSource;
import com.fahimkhan.smartpantry.models.PantryItem;
import com.fahimkhan.smartpantry.models.Recipe;
import com.fahimkhan.smartpantry.utils.NavigationHelper;
import com.fahimkhan.smartpantry.utils.RecipeMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows only the recipes the user can make right now, using the strict-matching
 * rule. Recalculated every time the screen becomes visible.
 */
public class SuggestedRecipesActivity extends AppCompatActivity
        implements RecipeAdapter.OnRecipeClickListener {

    private static final String TAG = "SuggestedRecipes";

    private RecyclerView recyclerSuggestions;
    private TextView textEmptySuggestions;
    private RecipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_suggested_recipes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(bars.left, bars.top, bars.right, 0);   // Bottom handled by the nav bar
            return insets;
        });

        recyclerSuggestions = findViewById(R.id.recyclerSuggestions);
        textEmptySuggestions = findViewById(R.id.textEmptySuggestions);
        recyclerSuggestions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(new ArrayList<>(), this);
        recyclerSuggestions.setAdapter(adapter);

        NavigationHelper.setUp(this, findViewById(R.id.bottomNav), R.id.nav_recipes);
    }

    /**
     * Recalculates suggestions every time the screen is shown, because the pantry
     * may have changed on another screen since this one was last visible.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    /** Loads recipes and pantry, applies the strict matcher, and updates the screen. */
    private void loadSuggestions() {
        RecipeDataSource recipeSource = new RecipeDataSource(this);
        PantryDataSource pantrySource = new PantryDataSource(this);
        List<Recipe> suggestions = new ArrayList<>();
        boolean pantryIsEmpty = false;

        try {
            recipeSource.open();
            pantrySource.open();
            List<PantryItem> pantry = pantrySource.getAllPantryItems();
            pantryIsEmpty = pantry.isEmpty();
            suggestions = RecipeMatcher.findMakeableRecipes(
                    recipeSource.getAllRecipesWithIngredients(), pantry);
        } catch (SQLException e) {
            Log.e(TAG, "Failed to load suggestions", e);
            Toast.makeText(this, R.string.error_loading_recipes, Toast.LENGTH_LONG).show();
        } finally {
            recipeSource.close();
            pantrySource.close();
        }

        adapter.setRecipes(suggestions);

        boolean noMatches = suggestions.isEmpty();
        if (noMatches) {
            textEmptySuggestions.setText(pantryIsEmpty
                    ? R.string.empty_suggestions_no_pantry
                    : R.string.empty_suggestions);
        }
        textEmptySuggestions.setVisibility(noMatches ? View.VISIBLE : View.GONE);
        recyclerSuggestions.setVisibility(noMatches ? View.GONE : View.VISIBLE);
    }

    /** Opens the detail screen, passing only the recipe's id. */
    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}
