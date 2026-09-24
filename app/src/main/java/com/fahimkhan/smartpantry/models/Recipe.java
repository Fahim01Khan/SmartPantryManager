package com.fahimkhan.smartpantry.models;

import java.util.ArrayList;
import java.util.List;

/**
 * A recipe with its preparation instructions and the list of
 * ingredients it requires.
 */
public class Recipe {

    private int id;
    private String name;
    private String instructions;
    private List<RecipeIngredient> ingredients;

    public Recipe() {
        this.id = -1;
        this.ingredients = new ArrayList<>();
    }

    public Recipe(int id, String name, String instructions) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
        this.ingredients = new ArrayList<>();
    }

    /** Adds one required ingredient to this recipe. */
    public void addIngredient(RecipeIngredient ingredient) {
        ingredients.add(ingredient);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public List<RecipeIngredient> getIngredients() { return ingredients; }
    public void setIngredients(List<RecipeIngredient> ingredients) { this.ingredients = ingredients; }
}
