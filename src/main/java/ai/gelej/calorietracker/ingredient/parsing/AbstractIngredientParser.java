package ai.gelej.calorietracker.ingredient.parsing;

import ai.gelej.calorietracker.ingredient.NutritionFacts;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Shared parsing logic for single-language parsers. Subclasses supply the language-specific
 * synonyms for nutrition-fact names and measure units; this class builds the matching patterns and
 * applies the common structural rules: the first non-blank line is the ingredient name, the next
 * four non-blank lines carry the four facts in any order, lines ending with a sparkles emoji are
 * dropped as comments, and a present-but-unrecognized unit fails the parse so the AI agent can take
 * over.
 */
public abstract class AbstractIngredientParser implements IngredientParser {

    private static final String SPARKLES = "✨";
    private static final String NAME_AMOUNT_SEPARATOR = "\\s*[-–—:,]?\\s*";
    private static final String AMOUNT = "([0-9]+(?:[.,][0-9]+)?)";

    private final Map<NutritionFact, Pattern> patterns;

    protected AbstractIngredientParser() {
        this.patterns = new EnumMap<>(NutritionFact.class);
        for (NutritionFact fact : NutritionFact.values()) {
            patterns.put(fact, compile(namesOf(fact), unitsOf(fact.getUnitKind())));
        }
    }

    /**
     * @param fact the nutrition fact
     * @return the accepted names of the fact in this parser's language
     */
    protected abstract List<String> namesOf(NutritionFact fact);

    /**
     * @param unitKind the kind of measure unit
     * @return the accepted unit names of the kind in this parser's language
     */
    protected abstract List<String> unitsOf(NutritionFact.UnitKind unitKind);

    @Override
    public Optional<NutritionFacts> parse(String text) {
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
        if (values.size() != NutritionFact.values().length) {
            return Optional.empty();
        }
        return Optional.of(new NutritionFacts(lines.get(0), values.get(NutritionFact.CALORIES),
                values.get(NutritionFact.FAT), values.get(NutritionFact.CARBS),
                values.get(NutritionFact.PROTEIN)));
    }

    private boolean matchFact(String line, Map<NutritionFact, BigDecimal> values) {
        for (NutritionFact fact : NutritionFact.values()) {
            Matcher matcher = patterns.get(fact).matcher(line);
            if (matcher.matches()) {
                if (values.containsKey(fact)) {
                    return false;
                }
                values.put(fact, new BigDecimal(matcher.group(1).replace(',', '.')));
                return true;
            }
        }
        return false;
    }

    private static Pattern compile(List<String> names, List<String> units) {
        return Pattern.compile("^(?:" + alternation(names) + ")" + NAME_AMOUNT_SEPARATOR + AMOUNT
                        + "(?:\\s*(?:" + alternation(units) + "))?$",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }

    private static String alternation(List<String> tokens) {
        return tokens.stream()
                .sorted(Comparator.comparingInt(String::length).reversed())
                .map(Pattern::quote)
                .collect(Collectors.joining("|"));
    }
}
