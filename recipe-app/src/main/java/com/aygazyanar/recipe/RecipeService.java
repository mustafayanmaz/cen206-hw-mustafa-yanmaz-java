/**
 * @file RecipeService.java
 * @brief Provides service-layer operations for managing recipes.
 */

package com.aygazyanar.recipe;

import java.util.List;

/**
 * @class RecipeService
 * @brief Provides service-layer operations for managing recipes.
 *
 * Acts as a bridge between the application logic and the recipe data access layer.
 */
public class RecipeService {

    /**
     * @brief Data Access Object used for recipe persistence operations.
     */
    private RecipeDAO recipeDAO;

    /**
     * @brief Constructs the service and initializes the DAO implementation.
     * @throws Exception If DAO initialization fails.
     */
    public RecipeService() throws Exception {
        this.recipeDAO = new RecipeDAOImpl();
    }

    /**
     * @brief Adds a new recipe to the system.
     * @param recipe The Recipe object to be added.
     */
    public void addRecipe(Recipe recipe) {
        try {
            recipeDAO.addRecipe(recipe);
            System.out.println("Recipe added successfully.");
        } catch (Exception e) {
            System.out.println("Error adding recipe: " + e.getMessage());
        }
    }

    /**
     * @brief Retrieves a recipe by its ID.
     * @param id The ID of the recipe to retrieve.
     * @return The Recipe object if found, otherwise null.
     */
    public Recipe getRecipeById(int id) {
        try {
            return recipeDAO.getRecipeById(id);
        } catch (Exception e) {
            System.out.println("Error retrieving recipe: " + e.getMessage());
            return null;
        }
    }

    /**
     * @brief Retrieves all recipes from the system.
     * @return A list of all Recipe objects, or null if retrieval fails.
     */
    public List<Recipe> getAllRecipes() {
        try {
            return recipeDAO.getAllRecipes();
        } catch (Exception e) {
            System.out.println("Error retrieving recipes: " + e.getMessage());
            return null;
        }
    }
}
