package ai.gelej.calorietracker.ingredient.parsing;

import ai.gelej.calorietracker.ingredient.Language;
import ai.gelej.calorietracker.ingredient.NutritionFacts;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses pre-cleaned message lines into nutrition facts, holding the structural rules shared by every
 * language: the first line is the ingredient name and the next four lines carry the four facts in any
 * order, and a present-but-unrecognized unit fails the parse so the AI agent can take over. Each fact
 * line is matched against {@link #linePattern()} filled with language-specific placeholders. This class
 * is the English implementation; subclasses override only the placeholders that differ.
 */
@Component
@Order(0)
public class IngredientParser {

    private final Map<NutritionFact, Pattern> patterns;

    public IngredientParser() {
        this.patterns = new EnumMap<>(NutritionFact.class);
        for (NutritionFact fact : NutritionFact.values()) {
            String regex = String.format(linePattern(), names(fact), nameAmountSeparators(), amount(),
                    amountUnitSeparators(), units(fact.getUnitKind()));
            patterns.put(fact, Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE));
        }
    }

    /**
     * @return the language this parser understands
     */
    public Language language() {
        return Language.ENGLISH;
    }

    /**
     * @return the line pattern with placeholders for, in order, the fact names, name-amount separators,
     * amount, amount-unit separators and unit names
     */
    protected String linePattern() {
        return "^(?:%1$s)\\s*(?:%2$s)?\\s*(%3$s)(?:(?:%4$s)(?:%5$s))?$";
    }

    /**
     * @return the accepted amount, as a regex matching an integer or decimal value
     */
    protected String amount() {
        return "[0-9]+(?:[.,][0-9]+)?";
    }

    /**
     * @param fact the nutrition fact
     * @return the accepted names of the fact, as a regex alternation of {@code |}-separated values
     */
    protected String names(NutritionFact fact) {
        return switch (fact) {
            case CALORIES -> "calories";
            case FAT -> "fat";
            case CARBS -> "carbs";
            case PROTEIN -> "protein";
        };
    }

    /**
     * @param unitKind the kind of measure unit
     * @return the accepted unit names of the kind, as a regex alternation of {@code |}-separated values
     */
    protected String units(NutritionFact.UnitKind unitKind) {
        return switch (unitKind) {
            case ENERGY -> "kcal";
            case MASS -> "g|gr|gram|grams";
        };
    }

    /**
     * @return the accepted separators between a fact name and its amount, as a regex alternation
     */
    protected String nameAmountSeparators() {
        return "-|–|—|:|,";
    }

    /**
     * @return the accepted separators between an amount and its unit, as a regex alternation
     */
    protected String amountUnitSeparators() {
        return "\\s*";
    }

    /**
     * Attempts to parse the pre-cleaned message lines into nutrition facts.
     *
     * @param lines the message content lines, already stripped of blank and comment lines
     * @return the parsed facts, or {@link Optional#empty()} if the lines are not in this parser's
     * language or do not match the expected format
     */
    public Optional<NutritionFacts> parse(List<String> lines) {
        if (lines.size() != NutritionFact.values().length + 1) {
            return Optional.empty();
        }
        Map<NutritionFact, BigDecimal> values = new EnumMap<>(NutritionFact.class);
        for (String line : lines.subList(1, lines.size())) {
            if (!matchFact(line, values)) {
                return Optional.empty();
            }
        }
        return Optional.of(new NutritionFacts(lines.get(0), values.get(NutritionFact.CALORIES),
                values.get(NutritionFact.FAT), values.get(NutritionFact.CARBS),
                values.get(NutritionFact.PROTEIN)));
    }

    private boolean matchFact(String line, Map<NutritionFact, BigDecimal> values) {
        for (NutritionFact fact : NutritionFact.values()) {
            Matcher matcher = patterns.get(fact).matcher(line);
            if (matcher.matches()) {
                return values.put(fact, new BigDecimal(matcher.group(1).replace(',', '.'))) == null;
            }
        }
        return false;
    }
}
