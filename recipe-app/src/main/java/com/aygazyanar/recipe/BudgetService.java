/**
 * @file BudgetService.java
 * @brief Provides functionality for budget planning based on recipe costs.
 */

package com.aygazyanar.recipe;

import java.util.List;

/**
 * @class BudgetService
 * @brief Provides functionality for planning meals within a given budget.
 *
 * This service class compares the total cost of a list of recipes against a specified budget
 * and outputs whether the budget is exceeded or not.
 */
public class BudgetService {

    /**
     * @brief Plans meals by calculating the total cost of given recipes and comparing it to the budget.
     *
     * This method calculates the total cost of all provided recipes and prints the cost
     * of each recipe, the total cost, and whether the total is within or over the specified budget.
     *
     * @param budget The total budget available.
     * @param recipes The list of recipes to be considered in the budget planning.
     */
    public void planBudget(double budget, List<Recipe> recipes) {
        double totalCost = 0;
        System.out.println("Planning meals within a budget of: " + budget);
        for (Recipe recipe : recipes) {
            double cost = recipe.calculateTotalCost();
            totalCost += cost;
            System.out.println("Recipe: " + recipe.getName() + ", Cost: " + cost);
        }
        System.out.println("Total cost: " + totalCost);
        if (totalCost > budget) {
            System.out.println("Warning: Total cost exceeds the budget.");
        } else {
            System.out.println("Within budget.");
        }
    }
}
