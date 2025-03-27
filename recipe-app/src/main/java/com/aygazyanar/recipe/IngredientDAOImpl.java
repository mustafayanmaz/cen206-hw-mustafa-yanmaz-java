/**
 * @file IngredientDAOImpl.java
 * @brief Implements IngredientDAO using SQLite via JDBC for managing ingredient data.
 */

package com.aygazyanar.recipe; ///< Main package for the Recipe Cost Calculator application.

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @class IngredientDAOImpl
 * @brief Implements the IngredientDAO interface using SQLite via JDBC.
 *
 * Provides methods to add, update, retrieve, and list ingredients in the database.
 */
public class IngredientDAOImpl implements IngredientDAO {

    /**
     * @brief JDBC connection used for database operations.
     */
    private Connection connection;

    /**
     * @brief Constructs the DAO and initializes the ingredients table.
     * @throws Exception If the database connection or table creation fails.
     */
    public IngredientDAOImpl() throws Exception {
        this.connection = DBConnection.getInstance().getConnection();
        createIngredientTable();
    }

    /**
     * @brief Creates the ingredients table if it does not already exist.
     * @throws Exception If table creation fails.
     */
    private void createIngredientTable() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS ingredients ("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                   + "name TEXT NOT NULL, "
                   + "price REAL NOT NULL, "
                   + "category TEXT"
                   + ")";
        connection.createStatement().execute(sql);
    }

    /**
     * @brief Adds a new ingredient to the database.
     * @param ingredient The Ingredient object to be added.
     * @throws Exception If the insert operation fails.
     */
    @Override
    public void addIngredient(Ingredient ingredient) throws Exception {
        String sql = "INSERT INTO ingredients (name, price, category) VALUES (?,?,?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, ingredient.getName());
        pstmt.setDouble(2, ingredient.getPrice());
        pstmt.setString(3, ingredient.getCategory());
        pstmt.executeUpdate();
    }

    /**
     * @brief Updates an existing ingredient in the database.
     * @param ingredient The Ingredient object with updated values.
     * @throws Exception If the update operation fails.
     */
    @Override
    public void updateIngredient(Ingredient ingredient) throws Exception {
        String sql = "UPDATE ingredients SET name = ?, price = ?, category = ? WHERE id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, ingredient.getName());
        pstmt.setDouble(2, ingredient.getPrice());
        pstmt.setString(3, ingredient.getCategory());
        pstmt.setInt(4, ingredient.getId());
        pstmt.executeUpdate();
    }

    /**
     * @brief Retrieves an ingredient by its ID from the database.
     * @param id The ID of the ingredient.
     * @return The Ingredient object if found, or null otherwise.
     * @throws Exception If the retrieval operation fails.
     */
    @Override
    public Ingredient getIngredientById(int id) throws Exception {
        String sql = "SELECT * FROM ingredients WHERE id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return new Ingredient(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getString("category")
            );
        }
        return null;
    }

    /**
     * @brief Retrieves all ingredients stored in the database.
     * @return A list of all Ingredient objects.
     * @throws Exception If the retrieval operation fails.
     */
    @Override
    public List<Ingredient> getAllIngredients() throws Exception {
        List<Ingredient> list = new ArrayList<>();
        String sql = "SELECT * FROM ingredients";
        ResultSet rs = connection.createStatement().executeQuery(sql);
        while (rs.next()) {
            list.add(new Ingredient(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getString("category")
            ));
        }
        return list;
    }
}
