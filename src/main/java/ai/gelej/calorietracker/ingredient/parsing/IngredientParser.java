package ai.gelej.calorietracker.ingredient.parsing;

import ai.gelej.calorietracker.ingredient.Language;
import ai.gelej.calorietracker.ingredient.NutritionFacts;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Parses a free-form message into nutrition facts. Each implementation handles a single language, so
 * a successful parse also identifies the language the message was written in.
 */
public interface IngredientParser {

    /**
     * @return the language this parser understands
     */
    Language language();

    /**
     * Attempts to parse the message into nutrition facts.
     *
     * @param text the raw message text
     * @return the parsed facts, or {@link Optional#empty()} if the message is not in this parser's
     * language or does not match the expected format
     */
    Optional<NutritionFacts> parse(@Nullable String text);
}
