package ai.gelej.calorietracker.ingredient.parsing;

import ai.gelej.calorietracker.ingredient.Language;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Parses English ingredient messages using the default placeholders of {@link AbstractIngredientParser}.
 */
@Component
@Order(0)
public class EnglishIngredientParser extends AbstractIngredientParser {

    @Override
    public Language language() {
        return Language.ENGLISH;
    }
}
