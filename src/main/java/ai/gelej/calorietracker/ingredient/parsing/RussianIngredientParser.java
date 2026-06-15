package ai.gelej.calorietracker.ingredient.parsing;

import ai.gelej.calorietracker.ingredient.Language;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Parses Russian ingredient messages by overriding the fact-name and unit placeholders.
 */
@Component
@Order(1)
public class RussianIngredientParser extends AbstractIngredientParser {

    @Override
    public Language language() {
        return Language.RUSSIAN;
    }

    @Override
    protected String names(NutritionFact fact) {
        return switch (fact) {
            case CALORIES -> "калории|к";
            case FAT -> "жир|жиры|ж";
            case CARBS -> "углеводы|угли|карбсы|у";
            case PROTEIN -> "белки|протеины|протеин|б";
        };
    }

    @Override
    protected String units(NutritionFact.UnitKind unitKind) {
        return switch (unitKind) {
            case ENERGY -> "ккал";
            case MASS -> "г|гр|грамм";
        };
    }
}
