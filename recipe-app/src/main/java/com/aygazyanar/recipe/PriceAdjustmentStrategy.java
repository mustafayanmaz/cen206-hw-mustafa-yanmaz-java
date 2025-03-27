/**
 * @file PriceAdjustmentStrategy.java
 * @brief Defines the strategy interface for adjusting ingredient prices.
 */

package com.aygazyanar.recipe; ///< Main package for the Recipe Cost Calculator application.

/**
 * @interface PriceAdjustmentStrategy
 * @brief Strategy interface for adjusting the price of an ingredient.
 *
 * Defines a contract for price modification logic, allowing different adjustment strategies
 * such as increasing or decreasing the price by a percentage.
 */
public interface PriceAdjustmentStrategy {

    /**
     * @brief Adjusts the current price based on the implemented strategy.
     * @param currentPrice The original price before adjustment.
     * @return The adjusted price.
     */
    double adjustPrice(double currentPrice);
}
