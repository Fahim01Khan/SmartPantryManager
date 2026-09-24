package com.fahimkhan.smartpantry.models;

/**
 * One ingredient that a recipe requires, including the minimum
 * quantity and the unit it is measured in.
 */
public class RecipeIngredient {

    private int id;
    private int recipeId;
    private String name;      // Stored in canonical form, e.g. "tomato"
    private double quantity;
    private String unit;

    public RecipeIngredient() {
        this.id = -1;
    }

    public RecipeIngredient(int id, int recipeId, String name, double quantity, String unit) {
        this.id = id;
        this.recipeId = recipeId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRecipeId() { return recipeId; }
    public void setRecipeId(int recipeId) { this.recipeId = recipeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
