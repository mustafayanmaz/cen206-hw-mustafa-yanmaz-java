/**
 * @file IncreasePriceStrategy.java
 * @brief Strategy implementation for increasing ingredient prices.
 */

package com.aygazyanar.recipe;

/**
 * @class IncreasePriceStrategy
 * @brief Strategy for increasing the price of an ingredient by a given percentage.
 *
 * Implements the PriceAdjustmentStrategy interface to increase the price based on the specified percentage.
 */
public class IncreasePriceStrategy implements PriceAdjustmentStrategy {

    /**
     * @brief The percentage by which the price will be increased.
     */
    private double percentage;

    /**
     * @brief Constructs an IncreasePriceStrategy with the specified percentage.
     * 
     * @param percentage The percentage to increase the price by.
     */
    public IncreasePriceStrategy(double percentage) {
        this.percentage = percentage;
    }

    /**
     * @brief Applies the increase strategy to the given price.
     * 
     * @param currentPrice The current price before adjustment.
     * @return The adjusted price after increasing by the specified percentage.
     */
    @Override
    public double adjustPrice(double currentPrice) {
        return currentPrice * (1 + percentage / 100);
    }
}
