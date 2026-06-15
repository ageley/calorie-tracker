package ai.gelej.calorietracker.ingredient.parsing;

import ai.gelej.calorietracker.ingredient.Language;
import ai.gelej.calorietracker.ingredient.NutritionFacts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class EnglishIngredientParserTest {

    private final EnglishIngredientParser parser = new EnglishIngredientParser();

    @Test
    void language_always_isEnglish() {
        //when
        Language language = parser.language();

        //then
        assertThat(language).isEqualTo(Language.ENGLISH);
    }

    @Test
    void parse_wellFormedMessage_extractsFactsInOrder() {
        //given
        String text = """
                Milk chocolate
                Calories: 438kcal
                Fat: 19g
                Carbs: 61g
                Protein: 5g""";

        //when
        Optional<NutritionFacts> facts = parser.parse(text);

        //then
        assertThat(facts).contains(new NutritionFacts("Milk chocolate",
                new BigDecimal("438"), new BigDecimal("19"), new BigDecimal("61"), new BigDecimal("5")));
    }

    @Test
    void parse_factsInAnyOrder_mapsThemByName() {
        //given
        String text = """
                Milk chocolate
                Protein: 5g
                Carbs: 61g
                Calories: 438kcal
                Fat: 19g""";

        //when
        Optional<NutritionFacts> facts = parser.parse(text);

        //then
        assertThat(facts).contains(new NutritionFacts("Milk chocolate",
                new BigDecimal("438"), new BigDecimal("19"), new BigDecimal("61"), new BigDecimal("5")));
    }

    @Test
    void parse_blankAndWhitespaceLines_areIgnored() {
        //given
        String text = "\n  \t \nMilk chocolate\n\nCalories: 438kcal\n \t\nFat: 19g\nCarbs: 61g\n\nProtein: 5g\n\n";

        //when
        Optional<NutritionFacts> facts = parser.parse(text);

        //then
        assertThat(facts).map(NutritionFacts::name).contains("Milk chocolate");
        assertThat(facts).map(NutritionFacts::caloriesKcal).contains(new BigDecimal("438"));
    }

    @Test
    void parse_commentLinesEndingWithSparkles_areIgnored() {
        //given
        String text = """
                Saved ✨
                Milk chocolate
                Calories: 438 kcal
                Fat: 19 g
                Carbs: 61 g
                Protein: 5 g""";

        //when
        Optional<NutritionFacts> facts = parser.parse(text);

        //then
        assertThat(facts).map(NutritionFacts::name).contains("Milk chocolate");
    }

    @Test
    void parse_variousSeparatorsAndDecimals_areAccepted() {
        //given
        String text = """
                Milk chocolate
                Calories — 438.5
                Fat, 19,2
                Carbs 61
                Protein-5""";

        //when
        Optional<NutritionFacts> facts = parser.parse(text);

        //then
        assertThat(facts).contains(new NutritionFacts("Milk chocolate",
                new BigDecimal("438.5"), new BigDecimal("19.2"), new BigDecimal("61"), new BigDecimal("5")));
    }

    @Test
    void parse_unitSynonymsAndMissingUnits_areAccepted() {
        //given
        String text = """
                Milk chocolate
                Calories 438 kcal
                Fat 19 grams
                Carbs 61gr
                Protein 5""";

        //when
        Optional<NutritionFacts> facts = parser.parse(text);

        //then
        assertThat(facts).map(NutritionFacts::carbsG).contains(new BigDecimal("61"));
        assertThat(facts).map(NutritionFacts::proteinG).contains(new BigDecimal("5"));
    }

    @Test
    void parse_unknownUnit_returnsEmpty() {
        //given
        String text = """
                Milk chocolate
                Calories: 438 cal
                Fat: 19 g
                Carbs: 61 g
                Protein: 5 g""";

        //when
        Optional<NutritionFacts> facts = parser.parse(text);

        //then
        assertThat(facts).isEmpty();
    }

    @Test
    void parse_missingFact_returnsEmpty() {
        //given
        String text = """
                Milk chocolate
                Calories: 438 kcal
                Fat: 19 g
                Carbs: 61 g""";

        //when
        Optional<NutritionFacts> facts = parser.parse(text);

        //then
        assertThat(facts).isEmpty();
    }

    @Test
    void parse_russianMessage_returnsEmpty() {
        //given
        String text = """
                Шоколадный брауни
                Калории: 438 ккал
                Жир: 19 г
                Углеводы: 61 г
                Белки: 5 г""";

        //when
        Optional<NutritionFacts> facts = parser.parse(text);

        //then
        assertThat(facts).isEmpty();
    }

    @Test
    void parse_nullText_returnsEmpty() {
        //when
        Optional<NutritionFacts> facts = parser.parse(null);

        //then
        assertThat(facts).isEmpty();
    }
}
