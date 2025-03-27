/**
 * @file IngredientService.java
 * @brief Service class providing ingredient management logic between application and data layer.
 */

package com.aygazyanar.recipe;

import java.util.List;

/**
 * @class IngredientService
 * @brief Provides service-layer operations for managing ingredients.
 *
 * Acts as an intermediary between the application logic and the data access layer.
 */
public class IngredientService {

    /**
     * @brief Data Access Object for ingredient persistence operations.
     */
    private IngredientDAO ingredientDAO;

    /**
     * @brief Constructs the service and initializes the DAO implementation.
     * @throws Exception If DAO initialization fails.
     */
    public IngredientService() throws Exception {
        this.ingredientDAO = new IngredientDAOImpl();
    }

    /**
     * @brief Adds a new ingredient to the system.
     * @param ingredient The Ingredient object to add.
     */
    public void addIngredient(Ingredient ingredient) {
        try {
            ingredientDAO.addIngredient(ingredient);
            System.out.println("Ingredient added successfully.");
        } catch (Exception e) {
            System.out.println("Error adding ingredient: " + e.getMessage());
        }
    }

    /**
     * @brief Updates an existing ingredient's details.
     * @param ingredient The Ingredient object with updated data.
     */
    public void updateIngredient(Ingredient ingredient) {
        try {
            ingredientDAO.updateIngredient(ingredient);
            System.out.println("Ingredient updated successfully.");
        } catch (Exception e) {
            System.out.println("Error updating ingredient: " + e.getMessage());
        }
    }

    /**
     * @brief Retrieves a list of all ingredients.
     * @return A List of Ingredient objects, or null if an error occurs.
     */
    public List<Ingredient> getAllIngredients() {
        try {
            return ingredientDAO.getAllIngredients();
        } catch (Exception e) {
            System.out.println("Error retrieving ingredients: " + e.getMessage());
            return null;
        }
    }

    /**
     * @brief Retrieves an ingredient by its ID.
     * @param id The ID of the ingredient to retrieve.
     * @return The Ingredient object if found, otherwise null.
     */
    public Ingredient getIngredientById(int id) {
        try {
            return ingredientDAO.getIngredientById(id);
        } catch (Exception e) {
            System.out.println("Error retrieving ingredient: " + e.getMessage());
            return null;
        }
    }

    /**
     * @brief Adjusts the price of an ingredient using the provided strategy.
     *
     * If the ingredient exists, its price is recalculated and updated.
     *
     * @param ingredientId The ID of the ingredient to update.
     * @param strategy The price adjustment strategy (increase or decrease).
     */
    public void adjustIngredientPrice(int ingredientId, PriceAdjustmentStrategy strategy) {
        try {
            Ingredient ingredient = ingredientDAO.getIngredientById(ingredientId);
            if (ingredient != null) {
                double newPrice = strategy.adjustPrice(ingredient.getPrice());
                ingredient.setPrice(newPrice);
                ingredientDAO.updateIngredient(ingredient);
                System.out.println("Price adjusted successfully.");
            } else {
                System.out.println("Ingredient not found.");
            }
        } catch (Exception e) {
            System.out.println("Error adjusting price: " + e.getMessage());
        }
    }
}
