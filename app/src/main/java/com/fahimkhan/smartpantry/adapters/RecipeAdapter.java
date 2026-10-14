package com.fahimkhan.smartpantry.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fahimkhan.smartpantry.R;
import com.fahimkhan.smartpantry.models.Recipe;
import com.fahimkhan.smartpantry.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Binds a list of Recipes to rows in the suggestions RecyclerView.
 * Taps are passed back to the Activity, which handles navigation.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    /**
     * Implemented by the Activity to open the tapped recipe.
     */
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Recipe> recipes;
    private final OnRecipeClickListener listener;

    public RecipeAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        Context context = holder.itemView.getContext();

        holder.textName.setText(recipe.getName());

        // e.g. "3 ingredients: egg, milk, butter"
        List<String> names = new ArrayList<>();
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            names.add(ingredient.getName());
        }
        int count = names.size();
        String countText = context.getResources()
                .getQuantityString(R.plurals.ingredient_count, count, count);
        holder.textSummary.setText(context.getString(
                R.string.recipe_summary, countText, TextUtils.join(", ", names)));

        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    /**
     * Replaces the displayed recipes, e.g. after the suggestions are recalculated.
     */
    public void setRecipes(List<Recipe> newRecipes) {
        recipes.clear();
        recipes.addAll(newRecipes);
        notifyDataSetChanged();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        final TextView textName;
        final TextView textSummary;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textRecipeName);
            textSummary = itemView.findViewById(R.id.textRecipeSummary);
        }
    }
}
