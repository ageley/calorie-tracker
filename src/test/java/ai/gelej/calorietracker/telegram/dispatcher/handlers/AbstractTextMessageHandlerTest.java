package ai.gelej.calorietracker.telegram.dispatcher.handlers;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractTextMessageHandlerTest {

    @Test
    void contentLines_blankAndSparkleCommentLines_areRemovedAndStripped() {
        //given
        String text = "Saved ✨\n\n  Milk chocolate \n \t \nCalories: 438 kcal\nFat: 19 g\nCarbs: 61 g\nProtein: 5 g\n";

        //when
        List<String> lines = AbstractTextMessageHandler.contentLines(text);

        //then
        assertThat(lines).containsExactly("Milk chocolate", "Calories: 438 kcal", "Fat: 19 g",
                "Carbs: 61 g", "Protein: 5 g");
    }
}
