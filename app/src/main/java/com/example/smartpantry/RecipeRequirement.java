package com.example.smartpantry;

/** One ingredient a recipe needs: name, quantity and unit (model class). */
public class RecipeRequirement {

    private String ingredientName;
    private double quantity;
    private String unit;

    public RecipeRequirement(String ingredientName,
                             double quantity,
                             String unit) {

        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }
}