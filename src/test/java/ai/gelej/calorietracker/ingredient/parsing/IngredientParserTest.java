package ai.gelej.calorietracker.ingredient.parsing;

import ai.gelej.calorietracker.ingredient.Language;
import ai.gelej.calorietracker.ingredient.NutritionFacts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class IngredientParserTest {

    private final IngredientParser parser = new IngredientParser();

    private NutritionFacts expected;

    @BeforeEach
    void setUp() {
        expected = new NutritionFacts("Milk chocolate",
                new BigDecimal("438"), new BigDecimal("19"), new BigDecimal("61"), new BigDecimal("5"));
    }

    @Test
    void language_always_isEnglish() {
        //when
        Language language = parser.language();

        //then
        assertThat(language).isEqualTo(Language.ENGLISH);
    }

    @Test
    void parse_wellFormedLines_extractsFactsInOrder() {
        //given
        List<String> lines = List.of(expected.name(),
                "Calories: " + expected.caloriesKcal() + "kcal",
                "Fat: " + expected.fatG() + "g",
                "Carbs: " + expected.carbsG() + "g",
                "Protein: " + expected.proteinG() + "g");

        //when
        Optional<NutritionFacts> facts = parser.parse(lines);

        //then
        assertThat(facts).contains(expected);
    }

    @Test
    void parse_factsInAnyOrder_mapsThemByName() {
        //given
        List<String> lines = List.of(expected.name(),
                "Protein: " + expected.proteinG() + "g",
                "Carbs: " + expected.carbsG() + "g",
                "Calories: " + expected.caloriesKcal() + "kcal",
                "Fat: " + expected.fatG() + "g");

        //when
        Optional<NutritionFacts> facts = parser.parse(lines);

        //then
        assertThat(facts).contains(expected);
    }

    @Test
    void parse_variousSeparatorsAndDecimals_areAccepted() {
        //given
        NutritionFacts withDecimals = new NutritionFacts(expected.name(),
                new BigDecimal("438.5"), new BigDecimal("19.2"), expected.carbsG(), expected.proteinG());
        List<String> lines = List.of(withDecimals.name(), "Calories — 438.5", "Fat, 19,2", "Carbs 61",
                "Protein-5");

        //when
        Optional<NutritionFacts> facts = parser.parse(lines);

        //then
        assertThat(facts).contains(withDecimals);
    }

    @Test
    void parse_unitSynonymsAndMissingUnits_areAccepted() {
        //given
        List<String> lines = List.of(expected.name(), "Calories 438 kcal", "Fat 19 grams", "Carbs 61gr",
                "Protein 5");

        //when
        Optional<NutritionFacts> facts = parser.parse(lines);

        //then
        assertThat(facts).map(NutritionFacts::carbsG).contains(expected.carbsG());
        assertThat(facts).map(NutritionFacts::proteinG).contains(expected.proteinG());
    }

    @Test
    void parse_unknownUnit_returnsEmpty() {
        //given
        List<String> lines = List.of(expected.name(), "Calories: 438 cal", "Fat: 19 g", "Carbs: 61 g",
                "Protein: 5 g");

        //when
        Optional<NutritionFacts> facts = parser.parse(lines);

        //then
        assertThat(facts).isEmpty();
    }

    @Test
    void parse_missingFact_returnsEmpty() {
        //given
        List<String> lines = List.of(expected.name(), "Calories: 438 kcal", "Fat: 19 g", "Carbs: 61 g");

        //when
        Optional<NutritionFacts> facts = parser.parse(lines);

        //then
        assertThat(facts).isEmpty();
    }

    @Test
    void parse_russianLines_returnsEmpty() {
        //given
        List<String> lines = List.of("Шоколадный брауни", "Калории: 438 ккал", "Жир: 19 г",
                "Углеводы: 61 г", "Белки: 5 г");

        //when
        Optional<NutritionFacts> facts = parser.parse(lines);

        //then
        assertThat(facts).isEmpty();
    }
}
