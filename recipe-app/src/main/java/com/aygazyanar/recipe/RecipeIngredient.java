/**
 * @file RecipeIngredient.java
 * @brief Represents the association between an ingredient and its usage in a recipe.
 */

package com.aygazyanar.recipe;

/**
 * @class RecipeIngredient
 * @brief Represents an ingredient used in a recipe, along with its quantity and unit.
 *
 * This class encapsulates an Ingredient object and additional information about how it is used
 * in a specific recipe.
 */
public class RecipeIngredient {

    /**
     * @brief The ingredient used in the recipe.
     */
    private Ingredient ingredient;

    /**
     * @brief The quantity of the ingredient used.
     */
    private double quantity;

    /**
     * @brief The unit of measurement for the quantity (e.g., grams, cups).
     */
    private String unit;

    /**
     * @brief Constructs a RecipeIngredient with specified ingredient, quantity, and unit.
     * @param ingredient The ingredient used in the recipe.
     * @param quantity The amount of the ingredient used.
     * @param unit The unit of measurement.
     */
    public RecipeIngredient(Ingredient ingredient, double quantity, String unit) {
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.unit = unit;
    }

    /**
     * @brief Gets the ingredient.
     * @return The Ingredient object.
     */
    public Ingredient getIngredient() {
        return ingredient;
    }

    /**
     * @brief Sets the ingredient.
     * @param ingredient The new Ingredient to set.
     */
    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    /**
     * @brief Gets the quantity of the ingredient.
     * @return The quantity used.
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * @brief Sets the quantity of the ingredient.
     * @param quantity The quantity to set.
     */
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    /**
     * @brief Gets the unit of measurement.
     * @return The unit as a string.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @brief Sets the unit of measurement.
     * @param unit The unit to set.
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }
}
