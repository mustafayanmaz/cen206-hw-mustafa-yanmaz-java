/**
 * @file RecipeDAO.java
 * @brief Interface for performing CRUD operations on recipes.
 */

package com.aygazyanar.recipe;

import java.util.List;

/**
 * @interface RecipeDAO
 * @brief Data Access Object interface for performing CRUD operations on recipes.
 *
 * Defines methods to add a new recipe, retrieve a recipe by ID,
 * and fetch all recipes from the data source.
 */
public interface RecipeDAO {

    /**
     * @brief Adds a new recipe to the data source.
     * 
     * @param recipe The Recipe object to be added.
     * @throws Exception If a database or connection error occurs.
     */
    void addRecipe(Recipe recipe) throws Exception;

    /**
     * @brief Retrieves a recipe by its ID.
     * 
     * @param id The ID of the recipe to retrieve.
     * @return The Recipe object if found, otherwise null.
     * @throws Exception If a database or connection error occurs.
     */
    Recipe getRecipeById(int id) throws Exception;

    /**
     * @brief Retrieves all recipes from the data source.
     * 
     * @return A list of all Recipe objects.
     * @throws Exception If a database or connection error occurs.
     */
    List<Recipe> getAllRecipes() throws Exception;
}
