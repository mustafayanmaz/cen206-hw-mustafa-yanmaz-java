/**
 * @file Recipe.java
 * @brief Represents a recipe that includes multiple ingredients, their quantities, and units.
 */

package com.aygazyanar.recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * @class Recipe
 * @brief Represents a recipe consisting of multiple ingredients and their quantities.
 *
 * Provides methods to manage ingredients within a recipe and calculate the total cost.
 */
public class Recipe {

    /**
     * @brief The unique identifier of the recipe.
     */
    private int id;

    /**
     * @brief The name of the recipe.
     */
    private String name;

    /**
     * @brief List of ingredients (with quantity and unit) used in the recipe.
     */
    private List<RecipeIngredient> ingredients;

    /**
     * @brief Default constructor that initializes the ingredient list.
     */
    public Recipe() {
        ingredients = new ArrayList<>();
    }

    /**
     * @brief Constructs a recipe with the specified ID and name.
     * @param id The recipe's unique identifier.
     * @param name The name of the recipe.
     */
    public Recipe(int id, String name) {
        this.id = id;
        this.name = name;
        this.ingredients = new ArrayList<>();
    }

    /**
     * @brief Gets the recipe ID.
     * @return The ID of the recipe.
     */
    public int getId() {
        return id;
    }

    /**
     * @brief Sets the recipe ID.
     * @param id The ID to assign to the recipe.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @brief Gets the name of the recipe.
     * @return The recipe name.
     */
    public String getName() {
        return name;
    }

    /**
     * @brief Sets the name of the recipe.
     * @param name The new name to assign to the recipe.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @brief Gets the list of ingredients in the recipe.
     * @return List of RecipeIngredient objects.
     */
    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    /**
     * @brief Sets the list of ingredients for the recipe.
     * @param ingredients List of RecipeIngredient objects.
     */
    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    /**
     * @brief Adds a new ingredient to the recipe.
     * @param recipeIngredient The ingredient with quantity and unit to add.
     */
    public void addIngredient(RecipeIngredient recipeIngredient) {
        ingredients.add(recipeIngredient);
    }

    /**
     * @brief Calculates the total cost of the recipe based on its ingredients.
     * @return The total cost of the recipe.
     */
    public double calculateTotalCost() {
        double total = 0;
        for (RecipeIngredient ri : ingredients) {
            total += ri.getQuantity() * ri.getIngredient().getPrice();
        }
        return total;
    }
}
