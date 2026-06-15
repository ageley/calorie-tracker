package ai.gelej.calorietracker.ingredient.parsing;

import ai.gelej.calorietracker.ingredient.Language;
import ai.gelej.calorietracker.ingredient.NutritionFacts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RussianIngredientParserTest {

    private final RussianIngredientParser parser = new RussianIngredientParser();

    private NutritionFacts expected;

    @BeforeEach
    void setUp() {
        expected = new NutritionFacts("Шоколадный брауни",
                new BigDecimal("438"), new BigDecimal("19"), new BigDecimal("61"), new BigDecimal("5"));
    }

    @Test
    void language_always_isRussian() {
        //when
        Language language = parser.language();

        //then
        assertThat(language).isEqualTo(Language.RUSSIAN);
    }

    @Test
    void parse_wellFormedLines_extractsFactsInOrder() {
        //given
        List<String> lines = List.of(expected.name(),
                "Калории: " + expected.caloriesKcal() + " ккал",
                "Жир: " + expected.fatG() + " г",
                "Углеводы: " + expected.carbsG() + " г",
                "Белки: " + expected.proteinG() + " г");

        //when
        Optional<NutritionFacts> facts = parser.parse(lines);

        //then
        assertThat(facts).contains(expected);
    }

    @Test
    void parse_abbreviatedNamesAndUnits_areAccepted() {
        //given
        List<String> lines = List.of(expected.name(), "к 438ккал", "ж 19гр", "у 61", "б 5г");

        //when
        Optional<NutritionFacts> facts = parser.parse(lines);

        //then
        assertThat(facts).contains(expected);
    }

    @Test
    void parse_mixedCaseAndCommaDecimals_areAccepted() {
        //given
        NutritionFacts withDecimals = new NutritionFacts(expected.name(),
                expected.caloriesKcal(), expected.fatG(), new BigDecimal("61.5"), new BigDecimal("5.2"));
        List<String> lines = List.of(withDecimals.name(), "УГЛЕВОДЫ: 61,5 г", "Калории: 438 ккал",
                "ЖИРЫ: 19 г", "Протеины: 5,2 г");

        //when
        Optional<NutritionFacts> facts = parser.parse(lines);

        //then
        assertThat(facts).map(NutritionFacts::carbsG).contains(withDecimals.carbsG());
        assertThat(facts).map(NutritionFacts::proteinG).contains(withDecimals.proteinG());
    }

    @Test
    void parse_unknownUnit_returnsEmpty() {
        //given
        List<String> lines = List.of(expected.name(), "Калории: 438 ккал", "Жир: 19 кг",
                "Углеводы: 61 г", "Белки: 5 г");

        //when
        Optional<NutritionFacts> facts = parser.parse(lines);

        //then
        assertThat(facts).isEmpty();
    }

    @Test
    void parse_englishLines_returnsEmpty() {
        //given
        List<String> lines = List.of("Milk chocolate", "Calories: 438 kcal", "Fat: 19 g", "Carbs: 61 g",
                "Protein: 5 g");

        //when
        Optional<NutritionFacts> facts = parser.parse(lines);

        //then
        assertThat(facts).isEmpty();
    }
}
