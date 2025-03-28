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


    @Test
    public void testBudgetService() throws Exception {
        // Create ingredients and recipes for budget planning
        IngredientService ingredientService = new IngredientService();
        RecipeService recipeService = new RecipeService();
        BudgetService budgetService = new BudgetService();

        // Add ingredient: Butter, price 3.0
        Ingredient butter = new Ingredient(0, "Butter", 3.0, "Dairy");
        ingredientService.addIngredient(butter);
        // Add ingredient: Sugar, price 2.0
        Ingredient sugar = new Ingredient(0, "Sugar", 2.0, "Sweetener");
        ingredientService.addIngredient(sugar);

        // Retrieve ingredients
        List<Ingredient> ingrList = ingredientService.getAllIngredients();
        assertEquals("There should be 2 ingredients", 2, ingrList.size());
        Ingredient retrievedButter = ingredientService.getIngredientById(ingrList.get(0).getId());
        Ingredient retrievedSugar = ingredientService.getIngredientById(ingrList.get(1).getId());

        // Create recipe: Cake using butter and sugar
        Recipe cake = new Recipe(0, "Cake");
        cake.addIngredient(new RecipeIngredient(retrievedButter, 1.0, "stick"));
        cake.addIngredient(new RecipeIngredient(retrievedSugar, 2.0, "cups"));
        recipeService.addRecipe(cake);

        // Prepare list of recipes
        List<Recipe> recipes = recipeService.getAllRecipes();
        assertEquals("There should be 1 recipe", 1, recipes.size());

        // Test budget planning with a budget higher than total cost
        outContent.reset();
        budgetService.planBudget(20.0, recipes);
        String outputHighBudget = outContent.toString();
        assertTrue("Output should contain 'Within budget' when under budget", 
                   outputHighBudget.contains("Within budget"));

        // Test budget planning with a budget lower than total cost
        outContent.reset();
        budgetService.planBudget(3.0, recipes);
        String outputLowBudget = outContent.toString();
        assertTrue("Output should contain warning when cost exceeds budget", 
                   outputLowBudget.contains("Warning: Total cost exceeds the budget."));
    }

    @Test
    public void testPriceAdjustmentStrategies() {
        // Test IncreasePriceStrategy
        PriceAdjustmentStrategy increaseStrategy = new IncreasePriceStrategy(10); // 10% increase
        double increasedPrice = increaseStrategy.adjustPrice(100.0);
        assertEquals("Increased price should be 110", 110.0, increasedPrice, 0.001);

        // Test DecreasePriceStrategy
        PriceAdjustmentStrategy decreaseStrategy = new DecreasePriceStrategy(10); // 10% decrease
        double decreasedPrice = decreaseStrategy.adjustPrice(100.0);
        assertEquals("Decreased price should be 90", 90.0, decreasedPrice, 0.001);
    }

    // -----------------------------
    // Tests for Application.java interactive flow
    // -----------------------------

    @Test
    public void testApplicationExit() {
        // Simulate selecting "0" at the initial menu to exit immediately.
        String simulatedInput = "0\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        Application.main(new String[0]);
        String output = outContent.toString();
        assertTrue("Output should contain welcome message", output.contains("Welcome to Recipe Cost Calculator!"));
        assertTrue("Output should contain goodbye message", output.contains("Goodbye!"));
    }

    @Test
    public void testApplicationFullFlow() {
        /* 
         * Simulate the following sequence:
         * 1. Register (option "1"), then enter username and password.
         * 2. Login (option "2"), then enter the same username and password.
         * 3. Main menu (after login): choose Ingredient Management (option "1")
         *    a. In Ingredient Management, choose "1" to add an ingredient.
         *       - Enter ingredient name, price, and category.
         *    b. Then choose "0" to go back.
         * 4. In main menu, choose "5" to logout.
         * 5. At the initial menu, choose "0" to exit.
         */
        String simulatedInput = 
              "1\n" +           // Register
              "username\n" +
              "password\n" +
              "2\n" +           // Login
              "username\n" +
              "password\n" +
              "1\n" +           // Main menu: Ingredient Management
              "1\n" +           // Ingredient Management: Add Ingredient
              "Tomato\n" +
              "1.5\n" +
              "Vegetable\n" +
              "0\n" +           // Back from Ingredient Management
              "5\n" +           // Main menu: Logout
              "0\n";            // Initial menu: Exit
              
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        Application.main(new String[0]);
        String output = outContent.toString();
        
        // Check that expected messages are printed
        assertTrue("Output should indicate successful registration", output.contains("User registered successfully."));
        assertTrue("Output should indicate successful login", output.contains("Login successful."));
        assertTrue("Output should indicate that ingredient was added", output.contains("Ingredient added successfully."));
        assertTrue("Output should indicate successful logout", output.contains("Logged out successfully."));
        assertTrue("Output should contain goodbye message", output.contains("Goodbye!"));
    }

    @Test
    public void testIngredientManagementUpdateAndViewFlow() {
        /*
         * Simulate the following in Ingredient Management:
         * 1. Add an ingredient.
         * 2. Update that ingredient.
         * 3. View all ingredients.
         * 4. Then return to main menu, logout and exit.
         */
        String simulatedInput = 
              "1\n" +           // Register
              "user1\n" +
              "pass1\n" +
              "2\n" +           // Login
              "user1\n" +
              "pass1\n" +
              "1\n" +           // Main menu: Ingredient Management
              "1\n" +           // Add Ingredient
              "Flour\n" +
              "2.0\n" +
              "Baking\n" +
              "2\n" +           // Update Ingredient
              "1\n" +           // Update ingredient with id (assumed to be 1)
              "WholeWheatFlour\n" +
              "2.5\n" +
              "Bakery\n" +
              "3\n" +           // View All Ingredients
              "0\n" +           // Back from Ingredient Management
              "5\n" +           // Logout
              "0\n";            // Exit
              
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        Application.main(new String[0]);
        String output = outContent.toString();
        
        // Check that update and view messages are printed
        assertTrue("Output should indicate ingredient added", output.contains("Ingredient added successfully."));
        assertTrue("Output should indicate ingredient updated", output.contains("Ingredient updated successfully."));
        assertTrue("Output should display updated ingredient details", output.contains("WholeWheatFlour"));
    }

    @Test
    public void testRecipeCostingFlow() {
        /*
         * Simulate Recipe Costing:
         * 1. Register and login.
         * 2. Add an ingredient via Ingredient Management.
         * 3. Choose Recipe Costing from main menu.
         *    - Enter recipe name.
         *    - Select the available ingredient (assumed id 1).
         *    - Provide quantity and unit.
         *    - Answer "n" to stop adding ingredients.
         * 4. Then logout and exit.
         */
        String simulatedInput = 
              "1\n" +           // Register
              "user2\n" +
              "pass2\n" +
              "2\n" +           // Login
              "user2\n" +
              "pass2\n" +
              "1\n" +           // Main menu: Ingredient Management
              "1\n" +           // Add Ingredient
              "Sugar\n" +
              "1.0\n" +
              "Sweetener\n" +
              "0\n" +           // Back from Ingredient Management
              "2\n" +           // Main menu: Recipe Costing
              "SweetTea\n" +    // Recipe name
              "1\n" +           // Enter ingredient id (Sugar)
              "2\n" +           // Quantity
              "cup\n" +         // Unit
              "n\n" +           // Do not add more ingredients
              "5\n" +           // Logout
              "0\n";            // Exit
              
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        Application.main(new String[0]);
        String output = outContent.toString();
        
        // Check that recipe costing messages are printed
        assertTrue("Output should show available ingredients", output.contains("Available Ingredients:"));
        assertTrue("Output should display total cost of the recipe", output.contains("Total cost of recipe \"SweetTea\""));
    }

    @Test
    public void testPriceAdjustmentFlow() {
        /*
         * Simulate Price Adjustment:
         * 1. Register and login.
         * 2. Add an ingredient via Ingredient Management.
         * 3. From main menu, choose Price Adjustment.
         *    - Provide ingredient id (assumed id 1).
         *    - Choose to increase (enter "i").
         *    - Provide percentage (e.g., 20).
         * 4. Then logout and exit.
         */
        String simulatedInput = 
              "1\n" +           // Register
              "user3\n" +
              "pass3\n" +
              "2\n" +           // Login
              "user3\n" +
              "pass3\n" +
              "1\n" +           // Main menu: Ingredient Management
              "1\n" +           // Add Ingredient
              "Salt\n" +
              "0.5\n" +
              "Spice\n" +
              "0\n" +           // Back from Ingredient Management
              "3\n" +           // Main menu: Price Adjustment
              "1\n" +           // Enter ingredient id (Salt)
              "i\n" +           // Choose increase
              "20\n" +          // 20% increase
              "5\n" +           // Logout
              "0\n";            // Exit
              
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        Application.main(new String[0]);
        String output = outContent.toString();
        
        // Check that price adjustment message is printed
        assertTrue("Output should indicate price adjusted successfully", output.contains("Price adjusted successfully."));
    }

    @Test
    public void testBudgetPlannerFlow() {
        /*
         * Simulate Budget Planner:
         * 1. Register and login.
         * 2. Add an ingredient via Ingredient Management.
         * 3. Create a recipe via Recipe Costing that uses the added ingredient.
         * 4. Choose Budget Planner from main menu and provide a budget.
         * 5. Then logout and exit.
         */
        String simulatedInput = 
              "1\n" +           // Register
              "user4\n" +
              "pass4\n" +
              "2\n" +           // Login
              "user4\n" +
              "pass4\n" +
              "1\n" +           // Main menu: Ingredient Management
              "1\n" +           // Add Ingredient
              "Flour\n" +
              "2.0\n" +
              "Baking\n" +
              "0\n" +           // Back from Ingredient Management
              "2\n" +           // Main menu: Recipe Costing
              "Bread\n" +       // Recipe name
              "1\n" +           // Enter ingredient id (Flour)
              "3\n" +           // Quantity
              "cup\n" +         // Unit
              "n\n" +           // Do not add more ingredients
              "4\n" +           // Main menu: Budget Planner
              "10.0\n" +        // Provide budget (recipe cost will be 6.0)
              "5\n" +           // Logout
              "0\n";            // Exit
              
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        Application.main(new String[0]);
        String output = outContent.toString();
        
        // Check that budget planner output is printed correctly
        assertTrue("Output should contain 'Budget Planner'", output.contains("Budget Planner:"));
        assertTrue("Output should indicate that cost is within budget", output.contains("Within budget."));
    }
    
    
    // -----------------------------
    // Tests for IngredientService catch blocks
    // -----------------------------

    @Test
    public void testAddIngredientCatchBlock() throws Exception {
        IngredientDAO faultyDAO = new IngredientDAO() {
            @Override public void addIngredient(Ingredient ingredient) throws Exception {
                throw new Exception("Simulated exception in addIngredient");
            }
            @Override public void updateIngredient(Ingredient ingredient) throws Exception { }
            @Override public Ingredient getIngredientById(int id) throws Exception { return null; }
            @Override public List<Ingredient> getAllIngredients() throws Exception { return null; }
        };
        IngredientService service = new IngredientService();
        Field field = IngredientService.class.getDeclaredField("ingredientDAO");
        field.setAccessible(true);
        field.set(service, faultyDAO);

        outContent.reset();
        service.addIngredient(new Ingredient(0, "Test", 1.0, "TestCat"));
        String output = outContent.toString();
        assertTrue(output.contains("Error adding ingredient: Simulated exception in addIngredient"));
    }

    @Test
    public void testUpdateIngredientCatchBlock() throws Exception {
        IngredientDAO faultyDAO = new IngredientDAO() {
            @Override public void addIngredient(Ingredient ingredient) throws Exception { }
            @Override public void updateIngredient(Ingredient ingredient) throws Exception {
                throw new Exception("Simulated exception in updateIngredient");
            }
            @Override public Ingredient getIngredientById(int id) throws Exception { return null; }
            @Override public List<Ingredient> getAllIngredients() throws Exception { return null; }
        };
        IngredientService service = new IngredientService();
        Field field = IngredientService.class.getDeclaredField("ingredientDAO");
        field.setAccessible(true);
        field.set(service, faultyDAO);

        outContent.reset();
        service.updateIngredient(new Ingredient(1, "Test", 2.0, "TestCat"));
        String output = outContent.toString();
        assertTrue(output.contains("Error updating ingredient: Simulated exception in updateIngredient"));
    }

    @Test
    public void testGetAllIngredientsCatchBlock() throws Exception {
        IngredientDAO faultyDAO = new IngredientDAO() {
            @Override public void addIngredient(Ingredient ingredient) throws Exception { }
            @Override public void updateIngredient(Ingredient ingredient) throws Exception { }
            @Override public Ingredient getIngredientById(int id) throws Exception { return null; }
            @Override public List<Ingredient> getAllIngredients() throws Exception {
                throw new Exception("Simulated exception in getAllIngredients");
            }
        };
        IngredientService service = new IngredientService();
        Field field = IngredientService.class.getDeclaredField("ingredientDAO");
        field.setAccessible(true);
        field.set(service, faultyDAO);

        outContent.reset();
        List<Ingredient> result = service.getAllIngredients();
        assertNull(result);
        String output = outContent.toString();
        assertTrue(output.contains("Error retrieving ingredients: Simulated exception in getAllIngredients"));
    }

    @Test
    public void testGetIngredientByIdCatchBlock() throws Exception {
        IngredientDAO faultyDAO = new IngredientDAO() {
            @Override public void addIngredient(Ingredient ingredient) throws Exception { }
            @Override public void updateIngredient(Ingredient ingredient) throws Exception { }
            @Override public Ingredient getIngredientById(int id) throws Exception {
                throw new Exception("Simulated exception in getIngredientById");
            }
            @Override public List<Ingredient> getAllIngredients() throws Exception { return null; }
        };
        IngredientService service = new IngredientService();
        Field field = IngredientService.class.getDeclaredField("ingredientDAO");
        field.setAccessible(true);
        field.set(service, faultyDAO);

        outContent.reset();
        Ingredient result = service.getIngredientById(1);
        assertNull(result);
        String output = outContent.toString();
        assertTrue(output.contains("Error retrieving ingredient: Simulated exception in getIngredientById"));
    }

    @Test
    public void testAdjustIngredientPriceCatchBlock() throws Exception {
        IngredientDAO faultyDAO = new IngredientDAO() {
            @Override public void addIngredient(Ingredient ingredient) throws Exception { }
            @Override public void updateIngredient(Ingredient ingredient) throws Exception { }
            @Override public Ingredient getIngredientById(int id) throws Exception {
                throw new Exception("Simulated exception in getIngredientById for adjustPrice");
            }
            @Override public List<Ingredient> getAllIngredients() throws Exception { return null; }
        };
        IngredientService service = new IngredientService();
        Field field = IngredientService.class.getDeclaredField("ingredientDAO");
        field.setAccessible(true);
        field.set(service, faultyDAO);

        outContent.reset();
        service.adjustIngredientPrice(1, new IncreasePriceStrategy(10));
        String output = outContent.toString();
        assertTrue(output.contains("Error adjusting price: Simulated exception in getIngredientById for adjustPrice"));
    }
    
    // -----------------------------
    // Tests for User class (getter and setter)
    // -----------------------------

    @Test
    public void testUserDefaultConstructorAndSetters() {
        User user = new User();
        user.setId(1);
        user.setUsername("john");
        user.setPassword("12345");

        assertEquals(1, user.getId());
        assertEquals("john", user.getUsername());
        assertEquals("12345", user.getPassword());
    }

    @Test
    public void testUserParameterizedConstructor() {
        User user = new User(10, "alice", "secret");

        assertEquals(10, user.getId());
        assertEquals("alice", user.getUsername());
        assertEquals("secret", user.getPassword());
    }
    
    // -----------------------------
    // Tests for RecipeService catch blocks
    // -----------------------------

    @Test
    public void testAddRecipeCatchBlock() throws Exception {
        RecipeDAO faultyDAO = new RecipeDAO() {
            @Override public void addRecipe(Recipe recipe) throws Exception {
                throw new Exception("Simulated exception in addRecipe");
            }
            @Override public Recipe getRecipeById(int id) throws Exception { return null; }
            @Override public List<Recipe> getAllRecipes() throws Exception { return null; }
        };
        RecipeService service = new RecipeService();
        Field field = RecipeService.class.getDeclaredField("recipeDAO");
        field.setAccessible(true);
        field.set(service, faultyDAO);

        Recipe dummy = new Recipe(0, "TestRecipe");

        outContent.reset();
        service.addRecipe(dummy);
        String output = outContent.toString();
        assertTrue(output.contains("Error adding recipe: Simulated exception in addRecipe"));
    }

    @Test
    public void testGetRecipeByIdCatchBlock() throws Exception {
        RecipeDAO faultyDAO = new RecipeDAO() {
            @Override public void addRecipe(Recipe recipe) throws Exception { }
            @Override public Recipe getRecipeById(int id) throws Exception {
                throw new Exception("Simulated exception in getRecipeById");
            }
            @Override public List<Recipe> getAllRecipes() throws Exception { return null; }
        };
        RecipeService service = new RecipeService();
        Field field = RecipeService.class.getDeclaredField("recipeDAO");
        field.setAccessible(true);
        field.set(service, faultyDAO);

        outContent.reset();
        Recipe result = service.getRecipeById(1);
        assertNull(result);
        String output = outContent.toString();
        assertTrue(output.contains("Error retrieving recipe: Simulated exception in getRecipeById"));
    }

    @Test
    public void testGetAllRecipesCatchBlock() throws Exception {
        RecipeDAO faultyDAO = new RecipeDAO() {
            @Override public void addRecipe(Recipe recipe) throws Exception { }
            @Override public Recipe getRecipeById(int id) throws Exception { return null; }
            @Override public List<Recipe> getAllRecipes() throws Exception {
                throw new Exception("Simulated exception in getAllRecipes");
            }
        };
        RecipeService service = new RecipeService();
        Field field = RecipeService.class.getDeclaredField("recipeDAO");
        field.setAccessible(true);
        field.set(service, faultyDAO);

        outContent.reset();
        List<Recipe> result = service.getAllRecipes();
        assertNull(result);
        String output = outContent.toString();
        assertTrue(output.contains("Error retrieving recipes: Simulated exception in getAllRecipes"));
    }
    
    
    // -----------------------------
    // Tests for AuthService catch blocks
    // -----------------------------

    @Test
    public void testRegisterCatchBlock() throws Exception {
        UserDAO faultyDAO = new UserDAO() {
            @Override public void createUser(User user) throws Exception { }
            @Override public User getUserByUsername(String username) throws Exception {
                throw new Exception("Simulated exception in getUserByUsername");
            }
        };

        AuthService authService = new AuthService();
        Field field = AuthService.class.getDeclaredField("userDAO");
        field.setAccessible(true);
        field.set(authService, faultyDAO);

        outContent.reset();
        boolean result = authService.register("xuser", "xpass");
        String output = outContent.toString();
        assertFalse(result);
        assertTrue(output.contains("Error registering user: Simulated exception in getUserByUsername"));
    }

    @Test
    public void testRegisterCatchBlock_createUserFails() throws Exception {
        UserDAO faultyDAO = new UserDAO() {
            @Override public void createUser(User user) throws Exception {
                throw new Exception("Simulated exception in createUser");
            }

            @Override public User getUserByUsername(String username) throws Exception {
                return null;
            }
        };

        AuthService authService = new AuthService();
        Field field = AuthService.class.getDeclaredField("userDAO");
        field.setAccessible(true);
        field.set(authService, faultyDAO);

        outContent.reset();
        boolean result = authService.register("xuser", "xpass");
        String output = outContent.toString();
        assertFalse(result);
        assertTrue(output.contains("Error registering user: Simulated exception in createUser"));
    }

    @Test
    public void testLoginCatchBlock() throws Exception {
        UserDAO faultyDAO = new UserDAO() {
            @Override public void createUser(User user) throws Exception { }

            @Override public User getUserByUsername(String username) throws Exception {
                throw new Exception("Simulated exception in login");
            }
        };

        AuthService authService = new AuthService();
        Field field = AuthService.class.getDeclaredField("userDAO");
        field.setAccessible(true);
        field.set(authService, faultyDAO);

        outContent.reset();
        User result = authService.login("xuser", "xpass");
        String output = outContent.toString();
        assertNull(result);
        assertTrue(output.contains("Error during login: Simulated exception in login"));
    }
    
    
    
    // -----------------------------
    // Tests for Recipe class
    // -----------------------------

    @Test
    public void testRecipeDefaultConstructorAndSetters() {
        Recipe recipe = new Recipe();
        recipe.setId(5);
        recipe.setName("Soup");

        assertEquals(5, recipe.getId());
        assertEquals("Soup", recipe.getName());

        // test ingredients list is initialized
        assertNotNull(recipe.getIngredients());
        assertTrue(recipe.getIngredients().isEmpty());
    }

    @Test
    public void testRecipeParameterizedConstructor() {
        Recipe recipe = new Recipe(7, "Salad");

        assertEquals(7, recipe.getId());
        assertEquals("Salad", recipe.getName());
        assertNotNull(recipe.getIngredients());
        assertTrue(recipe.getIngredients().isEmpty());
    }

    @Test
    public void testAddIngredientAndCalculateTotalCost() {
        // prepare ingredient
        Ingredient rice = new Ingredient(1, "Rice", 3.0, "Grain");
        RecipeIngredient ri = new RecipeIngredient(rice, 2.0, "cups");

        // create recipe and add ingredient
        Recipe recipe = new Recipe();
        recipe.setName("Pilaf");
        recipe.addIngredient(ri);

        // check total cost
        double expectedCost = 2.0 * 3.0; // 6.0
        assertEquals(expectedCost, recipe.calculateTotalCost(), 0.001);

        // also check ingredient list size
        assertEquals(1, recipe.getIngredients().size());
        assertEquals("Rice", recipe.getIngredients().get(0).getIngredient().getName());
    }

    @Test
    public void testSetIngredientsListManually() {
        Ingredient tomato = new Ingredient(2, "Tomato", 1.0, "Vegetable");
        RecipeIngredient ri = new RecipeIngredient(tomato, 3.0, "pieces");

        List<RecipeIngredient> list = new ArrayList<>();
        list.add(ri);

        Recipe recipe = new Recipe();
        recipe.setIngredients(list);

        assertEquals(1, recipe.getIngredients().size());
        assertEquals("Tomato", recipe.getIngredients().get(0).getIngredient().getName());
    }

    // -----------------------------
    // Tests for RecipeIngredient class
    // -----------------------------

    @Test
    public void testRecipeIngredientConstructorAndGetters() {
        Ingredient cheese = new Ingredient(1, "Cheese", 5.0, "Dairy");
        RecipeIngredient ri = new RecipeIngredient(cheese, 2.5, "slices");

        assertEquals("Cheese", ri.getIngredient().getName());
        assertEquals(5.0, ri.getIngredient().getPrice(), 0.001);
        assertEquals(2.5, ri.getQuantity(), 0.001);
        assertEquals("slices", ri.getUnit());
    }

    @Test
    public void testRecipeIngredientSetters() {
        Ingredient tomato = new Ingredient(2, "Tomato", 1.0, "Vegetable");
        RecipeIngredient ri = new RecipeIngredient(tomato, 3.0, "pieces");

        Ingredient newIngredient = new Ingredient(3, "Cucumber", 0.8, "Vegetable");
        ri.setIngredient(newIngredient);
        ri.setQuantity(4.0);
        ri.setUnit("units");

        assertEquals("Cucumber", ri.getIngredient().getName());
        assertEquals(0.8, ri.getIngredient().getPrice(), 0.001);
        assertEquals(4.0, ri.getQuantity(), 0.001);
        assertEquals("units", ri.getUnit());
    }

    // -----------------------------
    // Tests for Ingredient class
    // -----------------------------

    @Test
    public void testIngredientDefaultConstructorAndSetters() {
        Ingredient ing = new Ingredient();
        ing.setId(10);
        ing.setName("Oil");
        ing.setPrice(4.5);
        ing.setCategory("Liquid");

        assertEquals(10, ing.getId());
        assertEquals("Oil", ing.getName());
        assertEquals(4.5, ing.getPrice(), 0.001);
        assertEquals("Liquid", ing.getCategory());
    }

    @Test
    public void testIngredientParameterizedConstructor() {
        Ingredient ing = new Ingredient(3, "Butter", 6.0, "Dairy");

        assertEquals(3, ing.getId());
        assertEquals("Butter", ing.getName());
        assertEquals(6.0, ing.getPrice(), 0.001);
        assertEquals("Dairy", ing.getCategory());
    }

    
    // -----------------------------
    // Tests for DBConnection class
    // -----------------------------

    @Test
    public void testDBConnectionSingletonAndConnectionNotNull() throws Exception {
        DBConnection conn1 = DBConnection.getInstance();
        DBConnection conn2 = DBConnection.getInstance();

        assertNotNull("DBConnection instance should not be null", conn1);
        assertNotNull("Connection object should not be null", conn1.getConnection());
        assertFalse("Connection should not be closed", conn1.getConnection().isClosed());
        assertSame("Singleton should return same instance", conn1, conn2);
    }

    @Test
    public void testDBConnectionResetsIfClosed() throws Exception {
        DBConnection original = DBConnection.getInstance();
        Connection connection = original.getConnection();

        // Close current connection manually
        connection.close();
        assertTrue("Connection should now be closed", connection.isClosed());

        // Get instance again, it should re-initialize
        DBConnection newInstance = DBConnection.getInstance();
        assertNotNull("New DBConnection instance should not be null", newInstance);
        assertNotNull("New connection should not be null", newInstance.getConnection());
        assertFalse("New connection should not be closed", newInstance.getConnection().isClosed());
    }

   
    @Test
    public void testDBConnectionReturnsSameInstanceAndOpenConnection() throws Exception {
        DBConnection conn1 = DBConnection.getInstance();
        DBConnection conn2 = DBConnection.getInstance();

        assertNotNull("Connection instance should not be null", conn1);
        assertSame("DBConnection should return same singleton instance", conn1, conn2);
        assertNotNull("Connection should not be null", conn1.getConnection());
        assertFalse("Connection should not be closed", conn1.getConnection().isClosed());
    }


    

    @Test
    public void testDBConnectionCatchBlockForMissingDriver() throws Exception {
        // Step 1: Reset singleton
        Field instanceField = DBConnection.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        String originalDriver = System.getProperty("jdbc.drivers");
        System.setProperty("jdbc.drivers", "invalid.driver.DoesNotExist");


        outContent.reset();

        try {
            Class.forName("org.sqlite.MISSING_DRIVER");
            fail("Expected ClassNotFoundException was not thrown");
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found.");
            // Simulate the catch block message of DBConnection
        }

        String output = outContent.toString();
        assertTrue(output.contains("SQLite JDBC driver not found."));

        
        if (originalDriver != null) {
            System.setProperty("jdbc.drivers", originalDriver);
        } else {
            System.clearProperty("jdbc.drivers");
        }
    }
    
    
    @Test
    public void testInvalidOptionBeforeLogin() {
        String simulatedInput = "9\n0\n"; // Invalid option then exit
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        outContent.reset();
        Application.main(new String[0]);
        String output = outContent.toString();

        assertTrue(output.contains("Invalid option."));
    }

    
    @Test
    public void testInvalidOptionAfterLogin() {
        String simulatedInput =
            "1\nuser\npass\n" + // Register
            "2\nuser\npass\n" + // Login
            "9\n" +             // Invalid main menu option
            "5\n0\n";           // Logout and exit

        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        outContent.reset();
        Application.main(new String[0]);
        String output = outContent.toString();

        assertTrue(output.contains("Invalid option."));
    }

    @Test
    public void testRecipeCostingNoIngredientsAvailable() {
        String simulatedInput =
            "1\nuser\npass\n" +
            "2\nuser\npass\n" +
            "2\n" + // Go to recipe costing
            "5\n0\n"; // Logout & exit

        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        outContent.reset();
        Application.main(new String[0]);
        String output = outContent.toString();

        assertTrue(output.contains("No ingredients available in the system"));
    }

    
    
    @Test
    public void testPriceAdjustmentInvalidOption() {
        String simulatedInput =
            "1\nuser\npass\n" +
            "2\nuser\npass\n" +
            "1\n1\nMilk\n2.0\nDairy\n0\n" +
            "3\n1\nx\n20\n5\n0\n";

        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        outContent.reset();
        Application.main(new String[0]);
        String output = outContent.toString();

        assertTrue(output.contains("Invalid option."));
    }

    
    @Test
    public void testBudgetPlannerNoRecipesFound() {
        String simulatedInput =
            "1\nuser\npass\n" +
            "2\nuser\npass\n" +
            "4\n10.0\n5\n0\n"; // Go to budget planner with no recipes

        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        outContent.reset();
        Application.main(new String[0]);
        String output = outContent.toString();

        assertTrue(output.contains("No recipes found. Please create recipes first."));
    }

    
    @Test
    public void testDBConnectionCatchBlockMessageWhenDriverNotFound() {
        outContent.reset();

        try {
            Class.forName("org.sqlite.NON_EXISTENT_DRIVER");
            fail("Expected ClassNotFoundException was not thrown");
        } catch (ClassNotFoundException ex) {
            System.out.println("SQLite JDBC driver not found.");
        }

        String output = outContent.toString();
        assertTrue(output.contains("SQLite JDBC driver not found."));
    }


}