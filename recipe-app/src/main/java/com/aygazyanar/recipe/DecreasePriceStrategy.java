/**
 * @file DecreasePriceStrategy.java
 * @brief Implementation of a strategy to decrease ingredient price by a percentage.
 */

package com.aygazyanar.recipe;

/**
 * @class DecreasePriceStrategy
 * @brief Strategy for decreasing the price of an ingredient by a given percentage.
 *
 * Implements the PriceAdjustmentStrategy interface to reduce the price based on the specified percentage.
 */
public class DecreasePriceStrategy implements PriceAdjustmentStrategy {

    /**
     * @brief The percentage by which the price will be decreased.
     */
    private double percentage;

    /**
     * @brief Constructs a DecreasePriceStrategy with the specified percentage.
     * @param percentage The percentage to decrease the price by.
     */
    public DecreasePriceStrategy(double percentage) {
        this.percentage = percentage;
    }

    /**
     * @brief Applies the decrease strategy to the given price.
     * @param currentPrice The current price before adjustment.
     * @return The adjusted price after decreasing by the specified percentage.
     */
    @Override
    public double adjustPrice(double currentPrice) {
        return currentPrice * (1 - percentage / 100);
    }
}
