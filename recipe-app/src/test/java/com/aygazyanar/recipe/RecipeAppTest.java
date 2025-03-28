package com.aygazyanar.recipe;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;



public class RecipeAppTest {

    // Capture System.out for tests that check printed output
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private InputStream originalIn;

    @BeforeClass
    public static void setUpClass() throws Exception {
        // Delete the database file if it exists to start fresh
        File dbFile = new File("recipe_calculator.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Before
    public void setUp() throws Exception {
        // Redirect System.out to capture output and save original System.in
        System.setOut(new PrintStream(outContent));
        originalIn = System.in;

        // Clean database tables and reset SQLite autoincrement counters for test isolation
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stmt = connection.createStatement();
            // Delete rows (note: order matters if there are foreign key constraints)
            stmt.executeUpdate("DELETE FROM recipe_ingredients");
            stmt.executeUpdate("DELETE FROM recipes");
            stmt.executeUpdate("DELETE FROM ingredients");
            stmt.executeUpdate("DELETE FROM users");
            // Reset the autoincrement counters (works in SQLite)
            stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='ingredients'");
            stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='recipes'");
            stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='users'");
            stmt.close();
        } catch (Exception e) {
            // Ignore exceptions if tables do not exist yet
        }
        outContent.reset();
    }

    @After
    public void tearDown() {
        // Restore original System.out and System.in, and reset captured output
        System.setOut(originalOut);
        System.setIn(originalIn);
        outContent.reset();

        // Reset static fields in Application (currentUser, authService, etc.) via reflection
        try {
            Field currentUserField = Application.class.getDeclaredField("currentUser");
            currentUserField.setAccessible(true);
            currentUserField.set(null, null);
            Field authServiceField = Application.class.getDeclaredField("authService");
            authServiceField.setAccessible(true);
            authServiceField.set(null, null);
            Field ingredientServiceField = Application.class.getDeclaredField("ingredientService");
            ingredientServiceField.setAccessible(true);
            ingredientServiceField.set(null, null);
            Field recipeServiceField = Application.class.getDeclaredField("recipeService");
            recipeServiceField.setAccessible(true);
            recipeServiceField.set(null, null);
            Field budgetServiceField = Application.class.getDeclaredField("budgetService");
            budgetServiceField.setAccessible(true);
            budgetServiceField.set(null, null);
        } catch (Exception e) {
            // If reflection fails, log or ignore (for tests, this should not happen)
        }
    }

    // -----------------------------
    // Direct Service/DAO tests
    // -----------------------------

    @Test
    public void testDBConnection() throws Exception {
        Connection conn = DBConnection.getInstance().getConnection();
        assertNotNull("DB connection should not be null", conn);
    }
    

    @Test
    public void testAuthService() throws Exception {
        AuthService authService = new AuthService();
        // Register a new user
        boolean regResult = authService.register("testuser", "password");
        assertTrue("User registration should succeed", regResult);
        
        // Try to register the same user again
        boolean regDuplicate = authService.register("testuser", "password");
        assertFalse("Duplicate user registration should fail", regDuplicate);

        // Test login with correct credentials
        User user = authService.login("testuser", "password");
        assertNotNull("Login with correct credentials should return a user", user);
        assertEquals("Username should match", "testuser", user.getUsername());

        // Test login with incorrect credentials
        User invalidUser = authService.login("testuser", "wrongpassword");
        assertNull("Login with wrong password should return null", invalidUser);
    }

    @Test
    public void testIngredientService() throws Exception {
        IngredientService ingredientService = new IngredientService();
        // Create a new ingredient
        Ingredient flour = new Ingredient(0, "Flour", 2.0, "Baking");
        ingredientService.addIngredient(flour);
        
        // Retrieve all ingredients and check if it is added
        List<Ingredient> ingredients = ingredientService.getAllIngredients();
        assertNotNull("Ingredient list should not be null", ingredients);
        assertEquals("There should be one ingredient", 1, ingredients.size());
        
        Ingredient retrieved = ingredients.get(0);
        assertEquals("Name should be Flour", "Flour", retrieved.getName());
        assertEquals("Price should be 2.0", 2.0, retrieved.getPrice(), 0.001);
        assertEquals("Category should be Baking", "Baking", retrieved.getCategory());
        
        // Update the ingredient
        retrieved.setName("WholeWheat Flour");
        retrieved.setPrice(2.5);
        retrieved.setCategory("Bakery");
        ingredientService.updateIngredient(retrieved);
        
        Ingredient updated = ingredientService.getIngredientById(retrieved.getId());
        assertNotNull("Updated ingredient should not be null", updated);
        assertEquals("Name should be updated", "WholeWheat Flour", updated.getName());
        assertEquals("Price should be updated", 2.5, updated.getPrice(), 0.001);
        assertEquals("Category should be updated", "Bakery", updated.getCategory());

        // Test price adjustment: increase by 50%
        IncreasePriceStrategy incStrategy = new IncreasePriceStrategy(50);
        ingredientService.adjustIngredientPrice(updated.getId(), incStrategy);
        Ingredient increased = ingredientService.getIngredientById(updated.getId());
        assertEquals("Price after increase should be 3.75", 2.5 * 1.5, increased.getPrice(), 0.001);

        // Test price adjustment: decrease by 20%
        DecreasePriceStrategy decStrategy = new DecreasePriceStrategy(20);
        ingredientService.adjustIngredientPrice(updated.getId(), decStrategy);
        Ingredient decreased = ingredientService.getIngredientById(updated.getId());
        // New price should be previous price multiplied by 0.8
        assertEquals("Price after decrease should be adjusted", increased.getPrice() * 0.8, decreased.getPrice(), 0.001);
    }

    @Test
    public void testRecipeServiceAndCalculation() throws Exception {
        // Add ingredients using IngredientService
        IngredientService ingredientService = new IngredientService();
        // Ingredient 1: Egg, price 0.5
        Ingredient egg = new Ingredient(0, "Egg", 0.5, "Protein");
        ingredientService.addIngredient(egg);
        // Ingredient 2: Milk, price 1.0
        Ingredient milk = new Ingredient(0, "Milk", 1.0, "Dairy");
        ingredientService.addIngredient(milk);

        // Retrieve ingredients to get their IDs
        List<Ingredient> ingredients = ingredientService.getAllIngredients();
        assertEquals("There should be 2 ingredients", 2, ingredients.size());
        Ingredient retrievedEgg = ingredientService.getIngredientById(ingredients.get(0).getId());
        Ingredient retrievedMilk = ingredientService.getIngredientById(ingredients.get(1).getId());

        // Create a recipe using RecipeService
        RecipeService recipeService = new RecipeService();
        Recipe pancake = new Recipe(0, "Pancake");
        // Add 2 eggs and 1 cup of milk
        RecipeIngredient riEgg = new RecipeIngredient(retrievedEgg, 2.0, "pieces");
        RecipeIngredient riMilk = new RecipeIngredient(retrievedMilk, 1.0, "cup");
        pancake.addIngredient(riEgg);
        pancake.addIngredient(riMilk);
        recipeService.addRecipe(pancake);

        // Test total cost calculation
        double expectedCost = 2.0 * 0.5 + 1.0 * 1.0; // 1.0 + 1.0 = 2.0
        assertEquals("Total cost of recipe should be calculated", expectedCost, pancake.calculateTotalCost(), 0.001);

        // Retrieve recipe by id
        Recipe retrievedRecipe = recipeService.getRecipeById(pancake.getId());
        assertNotNull("Retrieved recipe should not be null", retrievedRecipe);
        assertEquals("Recipe name should match", "Pancake", retrievedRecipe.getName());
        assertEquals("Recipe should have 2 ingredients", 2, retrievedRecipe.getIngredients().size());

        // Retrieve all recipes and check count
        List<Recipe> recipes = recipeService.getAllRecipes();
        assertEquals("There should be one recipe", 1, recipes.size());
    }

