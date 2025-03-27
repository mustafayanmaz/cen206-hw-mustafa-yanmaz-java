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

    /**
     * @brief Handles user registration.
     * @param scanner Scanner object for user input.
     */
    private static void register(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        authService.register(username, password);
    }

    /**
     * @brief Handles user login.
     * @param scanner Scanner object for user input.
     */
    private static void login(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        currentUser = authService.login(username, password);
    }

    /**
     * @brief Handles ingredient management options (add, update, view).
     * @param scanner Scanner object for user input.
     */
    private static void ingredientManagement(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\nIngredient Management:");
            System.out.println("1. Add Ingredient");
            System.out.println("2. Update Ingredient");
            System.out.println("3. View All Ingredients");
            System.out.println("0. Back");
            System.out.print("Select an option: ");
            int option = Integer.parseInt(scanner.nextLine());
            switch (option) {
                case 1:
                    System.out.print("Enter ingredient name: ");
                    String name = scanner.nextLine().trim();
                    System.out.print("Enter price: ");
                    double price = Double.parseDouble(scanner.nextLine());
                    System.out.print("Enter category: ");
                    String category = scanner.nextLine().trim();
                    Ingredient ingredient = new Ingredient(0, name, price, category);
                    ingredientService.addIngredient(ingredient);
                    break;
                case 2:
                    System.out.print("Enter ingredient ID to update: ");
                    int id = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter new name: ");
                    String newName = scanner.nextLine().trim();
                    System.out.print("Enter new price: ");
                    double newPrice = Double.parseDouble(scanner.nextLine());
                    System.out.print("Enter new category: ");
                    String newCategory = scanner.nextLine().trim();
                    Ingredient ingToUpdate = new Ingredient(id, newName, newPrice, newCategory);
                    ingredientService.updateIngredient(ingToUpdate);
                    break;
                case 3:
                    List<Ingredient> ingredients = ingredientService.getAllIngredients();
                    if (ingredients == null || ingredients.isEmpty()) {
                        System.out.println("No ingredients found.");
                    } else {
                        for (Ingredient ing : ingredients) {
                            System.out.println("ID: " + ing.getId() + ", Name: " + ing.getName() +
                                    ", Price: " + ing.getPrice() + ", Category: " + ing.getCategory());
                        }
                    }
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    /**
     * @brief Handles recipe costing flow: select ingredients and calculate total cost.
     * @param scanner Scanner object for user input.
     */
    private static void recipeCosting(Scanner scanner) {
        System.out.println("\nRecipe Costing:");
        System.out.print("Enter recipe name: ");
        String recipeName = scanner.nextLine().trim();
        Recipe recipe = new Recipe(0, recipeName);

        List<Ingredient> allIngredients = ingredientService.getAllIngredients();
        if (allIngredients == null || allIngredients.isEmpty()) {
            System.out.println("No ingredients available in the system. Please add ingredients first.");
            return;
        }
        System.out.println("Available Ingredients:");
        for (Ingredient ing : allIngredients) {
            System.out.println("ID: " + ing.getId() + " - " + ing.getName() 
                + " (Price: " + ing.getPrice() + ", Category: " + ing.getCategory() + ")");
        }

        boolean addMore = true;
        while (addMore) {
            System.out.print("Enter ingredient ID: ");
            int ingredientId = Integer.parseInt(scanner.nextLine());
            Ingredient ingredient = ingredientService.getIngredientById(ingredientId);
            if (ingredient == null) {
                System.out.println("Ingredient not found. Please try again.");
            } else {
                System.out.print("Enter quantity: ");
                double quantity = Double.parseDouble(scanner.nextLine());
                System.out.print("Enter unit: ");
                String unit = scanner.nextLine().trim();
                recipe.addIngredient(new RecipeIngredient(ingredient, quantity, unit));
            }
            System.out.print("Add another ingredient? (y/n): ");
            String choice = scanner.nextLine();
            if (!choice.equalsIgnoreCase("y")) {
                addMore = false;
            }
        }

        if (recipe.getIngredients().isEmpty()) {
            System.out.println("No ingredients added to the recipe. Aborting recipe creation.");
        } else {
            recipeService.addRecipe(recipe);
            double totalCost = recipe.calculateTotalCost();
            System.out.println("Total cost of recipe \"" + recipe.getName() + "\": " + totalCost);
        }
    }

    /**
     * @brief Adjusts the price of an ingredient by a given percentage.
     * @param scanner Scanner object for user input.
     */
    private static void priceAdjustment(Scanner scanner) {
        System.out.println("\nPrice Adjustment:");
        System.out.print("Enter ingredient ID to adjust price: ");
        int ingredientId = Integer.parseInt(scanner.nextLine());
        System.out.print("Do you want to increase or decrease the price? (i/d): ");
        String type = scanner.nextLine().trim();
        System.out.print("Enter percentage: ");
        double percentage = Double.parseDouble(scanner.nextLine());
        if (type.equalsIgnoreCase("i")) {
            ingredientService.adjustIngredientPrice(ingredientId, new IncreasePriceStrategy(percentage));
        } else if (type.equalsIgnoreCase("d")) {
            ingredientService.adjustIngredientPrice(ingredientId, new DecreasePriceStrategy(percentage));
        } else {
            System.out.println("Invalid option.");
        }
    }

    /**
     * @brief Plans a budget using existing recipes and the given budget amount.
     * @param scanner Scanner object for user input.
     */
    private static void budgetPlanner(Scanner scanner) {
        System.out.println("\nBudget Planner:");
        System.out.print("Enter your budget: ");
        double budget = Double.parseDouble(scanner.nextLine());
        List<Recipe> recipes = recipeService.getAllRecipes();
        if (recipes == null || recipes.isEmpty()) {
            System.out.println("No recipes found. Please create recipes first.");
        } else {
            budgetService.planBudget(budget, recipes);
        }
    }
}
