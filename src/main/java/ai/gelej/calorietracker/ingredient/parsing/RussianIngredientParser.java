package ai.gelej.calorietracker.ingredient.parsing;

import ai.gelej.calorietracker.ingredient.Language;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Parses Russian ingredient messages.
 */
@Component
@Order(1)
public class RussianIngredientParser extends AbstractIngredientParser {

    private static final Map<NutritionFact, List<String>> NAMES = Map.of(
            NutritionFact.CALORIES, List.of("калории", "к"),
            NutritionFact.FAT, List.of("жир", "жиры", "ж"),
            NutritionFact.CARBS, List.of("углеводы", "угли", "карбсы", "у"),
            NutritionFact.PROTEIN, List.of("белки", "протеины", "протеин", "б"));

    private static final Map<NutritionFact.UnitKind, List<String>> UNITS = Map.of(
            NutritionFact.UnitKind.ENERGY, List.of("ккал"),
            NutritionFact.UnitKind.MASS, List.of("г", "гр", "грамм"));

    @Override
    public Language language() {
        return Language.RUSSIAN;
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
