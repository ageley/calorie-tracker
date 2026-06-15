package ai.gelej.calorietracker.ingredient;

import java.math.BigDecimal;

/**
 * Nutrition facts of an ingredient per 100 grams, as extracted from a message, in the canonical
 * Calories/Fat/Carbs/Protein order.
 *
 * @param name the ingredient name
 * @param caloriesKcal energy in kilocalories
 * @param fatG fat in grams
 * @param carbsG carbohydrates in grams
 * @param proteinG protein in grams
 */
public record NutritionFacts(
        String name,
        BigDecimal caloriesKcal,
        BigDecimal fatG,
        BigDecimal carbsG,
        BigDecimal proteinG) {
}
