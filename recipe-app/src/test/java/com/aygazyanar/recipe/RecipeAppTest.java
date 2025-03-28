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
