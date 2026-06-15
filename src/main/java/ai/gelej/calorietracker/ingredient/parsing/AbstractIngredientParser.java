package ai.gelej.calorietracker.ingredient.parsing;

import ai.gelej.calorietracker.ingredient.NutritionFacts;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Top-level parser holding the structural rules shared by every language: the first non-blank line is
 * the ingredient name, the next four non-blank lines carry the four facts in any order, lines ending
 * with a sparkles emoji are dropped as comments, and a present-but-unrecognized unit fails the parse
 * so the AI agent can take over. Each fact line is matched against {@link #LINE_PATTERN} filled with
 * language-specific placeholders. The defaults are English; subclasses override only the placeholders
 * that differ.
 */
public abstract class AbstractIngredientParser implements IngredientParser {

    private static final String SPARKLES = "✨";
    private static final String LINE_PATTERN = "^(?:%1$s)\\s*(?:%2$s)?\\s*(%3$s)(?:(?:%4$s)(?:%5$s))?$";
    private static final String AMOUNT = "[0-9]+(?:[.,][0-9]+)?";

    private final Map<NutritionFact, Pattern> patterns;

    protected AbstractIngredientParser() {
        this.patterns = new EnumMap<>(NutritionFact.class);
        for (NutritionFact fact : NutritionFact.values()) {
            String regex = String.format(LINE_PATTERN, names(fact), nameAmountSeparators(), AMOUNT,
                    amountUnitSeparators(), units(fact.getUnitKind()));
            patterns.put(fact, Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE));
        }
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

    @Override
    public Optional<NutritionFacts> parse(@Nullable String text) {
        if (text == null) {
            return Optional.empty();
        }
        List<String> lines = text.lines()
                .map(String::strip)
                .filter(line -> !line.isEmpty())
                .filter(line -> !line.endsWith(SPARKLES))
                .toList();
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
