package ai.gelej.calorietracker.ingredient;

import org.springframework.stereotype.Component;

/**
 * Renders ingredient confirmation messages in Russian by overriding the message pattern.
 */
@Component
public class RussianIngredientConfirmationFormatter extends IngredientConfirmationFormatter {

    private static final String PATTERN = """
            Сохранено ✨
            %s
            Калории: %s ккал
            Жир: %s г
            Углеводы: %s г
            Белки: %s г""";

    @Override
    public Language language() {
        return Language.RUSSIAN;
    }

    @Override
    protected String pattern() {
        return PATTERN;
    }
}
