/**
 * @file IngredientDAO.java
 * @brief Interface for ingredient-related database operations.
 */

package com.aygazyanar.recipe;

import java.util.List;

/**
 * @interface IngredientDAO
 * @brief Data Access Object interface for performing CRUD operations on ingredients.
 *
 * This interface defines methods for adding, updating, retrieving a single ingredient,
 * and retrieving all ingredients from the data source.
 */
public interface IngredientDAO {

    /**
     * @brief Adds a new ingredient to the data source.
     * @param ingredient The ingredient object to be added.
     * @throws Exception If a database or connection error occurs.
     */
    void addIngredient(Ingredient ingredient) throws Exception;

    /**
     * @brief Updates an existing ingredient in the data source.
     * @param ingredient The updated ingredient object.
     * @throws Exception If a database or connection error occurs.
     */
    void updateIngredient(Ingredient ingredient) throws Exception;

    /**
     * @brief Retrieves an ingredient by its ID.
     * @param id The ID of the ingredient to retrieve.
     * @return The matching Ingredient object, or null if not found.
     * @throws Exception If a database or connection error occurs.
     */
    Ingredient getIngredientById(int id) throws Exception;

    /**
     * @brief Retrieves all ingredients from the data source.
     * @return A list of all Ingredient objects.
     * @throws Exception If a database or connection error occurs.
     */
    List<Ingredient> getAllIngredients() throws Exception;
}
