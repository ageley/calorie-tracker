package ai.gelej.calorietracker.ingredient;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Renders a saved ingredient into a confirmation message that mirrors the accepted input format, so
 * it can be parsed back if forwarded; the leading comment line ends with a sparkles emoji so the
 * parser drops it. The pattern here is English; subclasses override it for other languages.
 */
@Component
public class IngredientConfirmationFormatter {

    private static final String PATTERN = """
            Saved ✨
            %s
            Calories: %s kcal
            Fat: %s g
            Carbs: %s g
            Protein: %s g""";

    /**
     * @return the language this formatter renders messages in
     */
    public Language language() {
        return Language.ENGLISH;
    }

    /**
     * @return the message pattern, a text block with placeholders for the name and the four values
     */
    protected String pattern() {
        return PATTERN;
    }

    /**
     * Builds the confirmation message for a saved ingredient.
     *
     * @param facts the saved nutrition facts
     * @return the confirmation message text
     */
    public String format(NutritionFacts facts) {
        return String.format(pattern(), facts.name(), plain(facts.caloriesKcal()),
                plain(facts.fatG()), plain(facts.carbsG()), plain(facts.proteinG()));
    }

    private static String plain(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }
}
