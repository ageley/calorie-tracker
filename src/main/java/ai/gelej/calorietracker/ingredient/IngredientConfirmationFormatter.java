package ai.gelej.calorietracker.ingredient;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Renders a saved ingredient into a confirmation message in the message's own language. The output
 * mirrors the accepted input format so it can be parsed back if forwarded; the leading comment line
 * ends with a sparkles emoji so the parser drops it.
 */
@Component
public class IngredientConfirmationFormatter {

    /**
     * Builds the confirmation message for a saved ingredient.
     *
     * @param facts the saved nutrition facts
     * @param language the language to render the message in
     * @return the confirmation message text
     */
    public String format(NutritionFacts facts, Language language) {
        return language.getSavedComment() + " ✨\n"
                + facts.name() + "\n"
                + language.getCaloriesLabel() + ": " + plain(facts.caloriesKcal()) + " " + language.getEnergyUnit() + "\n"
                + language.getFatLabel() + ": " + plain(facts.fatG()) + " " + language.getMassUnit() + "\n"
                + language.getCarbsLabel() + ": " + plain(facts.carbsG()) + " " + language.getMassUnit() + "\n"
                + language.getProteinLabel() + ": " + plain(facts.proteinG()) + " " + language.getMassUnit();
    }

    private static String plain(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }
}
