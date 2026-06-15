package ai.gelej.calorietracker.ingredient;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Tool that persists an ingredient's nutrition facts per 100 grams. Exposed both to the programmatic
 * handler and to the AI agent so both save through the same path.
 */
@Component
@RequiredArgsConstructor
public class SaveIngredientTool {

    private final IngredientRepository repository;

    /**
     * Saves an ingredient for the given chat in the canonical Calories/Fat/Carbs/Protein order.
     *
     * @param chatId the Telegram chat the ingredient belongs to
     * @param name the ingredient name
     * @param caloriesKcal energy in kilocalories per 100 grams
     * @param fatG fat in grams per 100 grams
     * @param carbsG carbohydrates in grams per 100 grams
     * @param proteinG protein in grams per 100 grams
     */
    @Tool(description = "Save an ingredient's nutrition facts per 100 grams for a Telegram chat")
    public void saveIngredient(
            @ToolParam(description = "Telegram chat id the ingredient belongs to") Long chatId,
            @ToolParam(description = "Ingredient name") String name,
            @ToolParam(description = "Calories in kilocalories per 100 grams") BigDecimal caloriesKcal,
            @ToolParam(description = "Fat in grams per 100 grams") BigDecimal fatG,
            @ToolParam(description = "Carbohydrates in grams per 100 grams") BigDecimal carbsG,
            @ToolParam(description = "Protein in grams per 100 grams") BigDecimal proteinG) {
        repository.save(new Ingredient(null, chatId, name, caloriesKcal, fatG, carbsG, proteinG,
                false, Instant.now()));
    }
}
