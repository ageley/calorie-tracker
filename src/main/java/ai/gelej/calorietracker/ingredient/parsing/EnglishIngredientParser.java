package ai.gelej.calorietracker.ingredient.parsing;

import ai.gelej.calorietracker.ingredient.Language;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Parses English ingredient messages.
 */
@Component
@Order(0)
public class EnglishIngredientParser extends AbstractIngredientParser {

    private static final Map<NutritionFact, List<String>> NAMES = Map.of(
            NutritionFact.CALORIES, List.of("calories"),
            NutritionFact.FAT, List.of("fat"),
            NutritionFact.CARBS, List.of("carbs"),
            NutritionFact.PROTEIN, List.of("protein"));

    private static final Map<NutritionFact.UnitKind, List<String>> UNITS = Map.of(
            NutritionFact.UnitKind.ENERGY, List.of("kcal"),
            NutritionFact.UnitKind.MASS, List.of("g", "gr", "gram", "grams"));

    @Override
    public Language language() {
        return Language.ENGLISH;
    }

    @Override
    protected List<String> namesOf(NutritionFact fact) {
        return NAMES.get(fact);
    }

    @Override
    protected List<String> unitsOf(NutritionFact.UnitKind unitKind) {
        return UNITS.get(unitKind);
    }
}
