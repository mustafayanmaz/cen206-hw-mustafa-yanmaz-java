/**
 * @file RecipeDAOImpl.java
 * @brief Provides SQLite-based implementation of the RecipeDAO interface.
 */

package com.aygazyanar.recipe; ///< Main package for the Recipe Cost Calculator application.

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @class RecipeDAOImpl
 * @brief Implementation of the RecipeDAO interface using SQLite and JDBC.
 *
 * Provides database access for storing, retrieving, and managing recipes and their ingredients.
 */
public class RecipeDAOImpl implements RecipeDAO {

    /**
     * @brief JDBC connection used for executing SQL statements.
     */
    private Connection connection;

    /**
     * @brief Constructor that initializes the database connection and ensures tables exist.
     * @throws Exception if a database connection error occurs.
     */
    public RecipeDAOImpl() throws Exception {
        this.connection = DBConnection.getInstance().getConnection();
        createRecipeTable();
        createRecipeIngredientsTable();
    }

    /**
     * @brief Creates the "recipes" table if it doesn't already exist.
     * @throws Exception if table creation fails.
     */
    private void createRecipeTable() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS recipes ("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                   + "name TEXT NOT NULL"
                   + ")";
        connection.createStatement().execute(sql);
    }

    /**
     * @brief Creates the "recipe_ingredients" table if it doesn't already exist.
     * @throws Exception if table creation fails.
     */
    private void createRecipeIngredientsTable() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS recipe_ingredients ("
                   + "recipe_id INTEGER, "
                   + "ingredient_id INTEGER, "
                   + "quantity REAL, "
                   + "unit TEXT, "
                   + "FOREIGN KEY(recipe_id) REFERENCES recipes(id)"
                   + ")";
        connection.createStatement().execute(sql);
    }

    /**
     * @brief Adds a recipe and its ingredients to the database.
     * 
     * @param recipe The Recipe object to be stored.
     * @throws Exception if a database error occurs.
     */
    @Override
    public void addRecipe(Recipe recipe) throws Exception {
        String recipeSql = "INSERT INTO recipes (name) VALUES (?)";
        PreparedStatement recipeStmt = connection.prepareStatement(recipeSql, PreparedStatement.RETURN_GENERATED_KEYS);
        recipeStmt.setString(1, recipe.getName());
        recipeStmt.executeUpdate();
        ResultSet generatedKeys = recipeStmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            int recipeId = generatedKeys.getInt(1);
            recipe.setId(recipeId);
            for (RecipeIngredient ri : recipe.getIngredients()) {
                String sql = "INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity, unit) VALUES (?,?,?,?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, recipeId);
                pstmt.setInt(2, ri.getIngredient().getId());
                pstmt.setDouble(3, ri.getQuantity());
                pstmt.setString(4, ri.getUnit());
                pstmt.executeUpdate();
            }
        }
    }

    /**
     * @brief Retrieves a recipe and its ingredients by ID.
     * 
     * @param id The ID of the recipe to retrieve.
     * @return The Recipe object if found, otherwise null.
     * @throws Exception if a database error occurs.
     */
    @Override
    public Recipe getRecipeById(int id) throws Exception {
        String sql = "SELECT * FROM recipes WHERE id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();
        Recipe recipe = null;
        if (rs.next()) {
            recipe = new Recipe(rs.getInt("id"), rs.getString("name"));
            String ingredientSql = "SELECT ri.quantity, ri.unit, i.id, i.name, i.price, i.category "
                                 + "FROM recipe_ingredients ri JOIN ingredients i ON ri.ingredient_id = i.id WHERE ri.recipe_id = ?";
            PreparedStatement ingredientStmt = connection.prepareStatement(ingredientSql);
            ingredientStmt.setInt(1, recipe.getId());
            ResultSet ingredientRs = ingredientStmt.executeQuery();
            while (ingredientRs.next()) {
                Ingredient ingredient = new Ingredient(
                        ingredientRs.getInt("id"),
                        ingredientRs.getString("name"),
                        ingredientRs.getDouble("price"),
                        ingredientRs.getString("category")
                );
                RecipeIngredient ri = new RecipeIngredient(ingredient, ingredientRs.getDouble("quantity"), ingredientRs.getString("unit"));
                recipe.addIngredient(ri);
            }
        }
        return recipe;
    }

    /**
     * @brief Retrieves all recipes from the database along with their ingredients.
     * 
     * @return A list of all Recipe objects.
     * @throws Exception if a database error occurs.
     */
    @Override
    public List<Recipe> getAllRecipes() throws Exception {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT * FROM recipes";
        ResultSet rs = connection.createStatement().executeQuery(sql);
        while (rs.next()) {
            Recipe recipe = new Recipe(rs.getInt("id"), rs.getString("name"));
            String ingredientSql = "SELECT ri.quantity, ri.unit, i.id, i.name, i.price, i.category "
                                 + "FROM recipe_ingredients ri JOIN ingredients i ON ri.ingredient_id = i.id WHERE ri.recipe_id = ?";
            PreparedStatement ingredientStmt = connection.prepareStatement(ingredientSql);
            ingredientStmt.setInt(1, recipe.getId());
            ResultSet ingredientRs = ingredientStmt.executeQuery();
            while (ingredientRs.next()) {
                Ingredient ingredient = new Ingredient(
                        ingredientRs.getInt("id"),
                        ingredientRs.getString("name"),
                        ingredientRs.getDouble("price"),
                        ingredientRs.getString("category")
                );
                RecipeIngredient ri = new RecipeIngredient(ingredient, ingredientRs.getDouble("quantity"), ingredientRs.getString("unit"));
                recipe.addIngredient(ri);
            }
            recipes.add(recipe);
        }
        return recipes;
    }
}
