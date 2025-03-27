/**
 * @file Ingredient.java
 * @brief Represents an ingredient used in recipes with its name, price, and category.
 */

package com.aygazyanar.recipe;

/**
 * @class Ingredient
 * @brief Represents an ingredient used in recipes with its name, price, and category.
 *
 * This class provides basic information about an ingredient and supports setting/getting its fields.
 */
public class Ingredient {

    /**
     * @brief The unique identifier of the ingredient.
     */
    private int id;

    /**
     * @brief The name of the ingredient.
     */
    private String name;

    /**
     * @brief The price of the ingredient.
     */
    private double price;

    /**
     * @brief The category the ingredient belongs to (e.g., dairy, vegetable).
     */
    private String category;

    /**
     * @brief Default constructor.
     */
    public Ingredient() {
    }

    /**
     * @brief Constructs an ingredient with specified values.
     * @param id The unique identifier.
     * @param name The name of the ingredient.
     * @param price The price of the ingredient.
     * @param category The category of the ingredient.
     */
    public Ingredient(int id, String name, double price, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    /**
     * @brief Gets the ID of the ingredient.
     * @return The ingredient ID.
     */
    public int getId() {
        return id;
    }

    /**
     * @brief Sets the ID of the ingredient.
     * @param id The new ID to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @brief Gets the name of the ingredient.
     * @return The ingredient name.
     */
    public String getName() {
        return name;
    }

    /**
     * @brief Sets the name of the ingredient.
     * @param name The new name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @brief Gets the price of the ingredient.
     * @return The ingredient price.
     */
    public double getPrice() {
        return price;
    }

    /**
     * @brief Sets the price of the ingredient.
     * @param price The new price to set.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * @brief Gets the category of the ingredient.
     * @return The ingredient category.
     */
    public String getCategory() {
        return category;
    }

    /**
     * @brief Sets the category of the ingredient.
     * @param category The new category to set.
     */
    public void setCategory(String category) {
        this.category = category;
    }
}
