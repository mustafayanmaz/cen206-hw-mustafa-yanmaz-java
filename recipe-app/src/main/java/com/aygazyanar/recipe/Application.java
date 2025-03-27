package com.aygazyanar.recipe;



import java.util.List;
import java.util.Scanner;

/**
 * @class Application
 * @brief Entry point of the Recipe Cost Calculator application.
 *
 * Provides a text-based menu for user registration, login, ingredient management,
 * recipe costing, price adjustment, and budget planning.
 */
public class Application {

    /** 
     * @brief Handles user authentication processes such as register and login.
     */
    private static AuthService authService;

    /**
     * @brief Manages operations related to ingredients (add, update, list).
     */
    private static IngredientService ingredientService;

    /**
     * @brief Handles recipe creation and costing features.
     */
    private static RecipeService recipeService;

    /**
     * @brief Provides functionality to plan meals within a user-defined budget.
     */
    private static BudgetService budgetService;

    /**
     * @brief Holds the currently logged-in user during application runtime.
     */
    private static User currentUser;

    /**
     * @brief Main method of the application. Initializes services and handles main menu navigation.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            authService = new AuthService();
            ingredientService = new IngredientService();
            recipeService = new RecipeService();
            budgetService = new BudgetService();
        } catch (Exception e) {
            System.out.println("Error initializing services: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Recipe Cost Calculator!");

        boolean exit = false;
        while (!exit) {
            if (currentUser == null) {
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("0. Exit");
                System.out.print("Select an option: ");
                int option = Integer.parseInt(scanner.nextLine());
                switch (option) {
                    case 1:
                        register(scanner);
                        break;
                    case 2:
                        login(scanner);
                        break;
                    case 0:
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            } else {
                System.out.println("\nMain Menu:");
                System.out.println("1. Ingredient Management");
                System.out.println("2. Recipe Costing");
                System.out.println("3. Price Adjustment");
                System.out.println("4. Budget Planner");
                System.out.println("5. Logout");
                System.out.println("0. Exit");
                System.out.print("Select an option: ");
                int option = Integer.parseInt(scanner.nextLine());
                switch (option) {
                    case 1:
                        ingredientManagement(scanner);
                        break;
                    case 2:
                        recipeCosting(scanner);
                        break;
                    case 3:
                        priceAdjustment(scanner);
                        break;
                    case 4:
                        budgetPlanner(scanner);
                        break;
                    case 5:
                        currentUser = null;
                        System.out.println("Logged out successfully.");
                        break;
                    case 0:
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            }
        }
        scanner.close();
        System.out.println("Goodbye!");
    }
}