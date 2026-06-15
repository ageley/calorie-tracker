package ai.gelej.calorietracker.ingredient;

import ai.gelej.calorietracker.ai.AiToolContext;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Tool that persists an ingredient's nutrition facts per 100 grams. Exposed both to the programmatic
 * handler and to the AI agent so both save through the same path. The owning chat is taken from the
 * trusted {@link ToolContext}, never from a model-supplied parameter.
 */
@Component
@RequiredArgsConstructor
public class SaveIngredientTool {

    private final IngredientRepository repository;

    /**
     * Saves an ingredient for the chat bound in the tool context, in the canonical
     * Calories/Fat/Carbs/Protein order.
     *
     * @param name the ingredient name
     * @param caloriesKcal energy in kilocalories per 100 grams
     * @param fatG fat in grams per 100 grams
     * @param carbsG carbohydrates in grams per 100 grams
     * @param proteinG protein in grams per 100 grams
     * @param toolContext the tool context carrying the trusted chat id
     */
    @Tool(description = "Save an ingredient's nutrition facts per 100 grams for the current chat")
    public void saveIngredient(
            @ToolParam(description = "Ingredient name") String name,
            @ToolParam(description = "Calories in kilocalories per 100 grams") BigDecimal caloriesKcal,
            @ToolParam(description = "Fat in grams per 100 grams") BigDecimal fatG,
            @ToolParam(description = "Carbohydrates in grams per 100 grams") BigDecimal carbsG,
            @ToolParam(description = "Protein in grams per 100 grams") BigDecimal proteinG,
            ToolContext toolContext) {
        save((Long) toolContext.getContext().get(AiToolContext.CHAT_ID), name, caloriesKcal, fatG,
                carbsG, proteinG);
    }

    /**
     * Saves an ingredient for the given chat. Used by the programmatic handler, which already knows
     * the trusted chat id.
     *
     * @param chatId the Telegram chat the ingredient belongs to
     * @param name the ingredient name
     * @param caloriesKcal energy in kilocalories per 100 grams
     * @param fatG fat in grams per 100 grams
     * @param carbsG carbohydrates in grams per 100 grams
     * @param proteinG protein in grams per 100 grams
     */
    public void save(Long chatId, String name, BigDecimal caloriesKcal, BigDecimal fatG,
                     BigDecimal carbsG, BigDecimal proteinG) {
        repository.save(Ingredient.builder()
                .chatId(chatId)
                .name(name)
                .caloriesKcal(caloriesKcal)
                .fatG(fatG)
                .carbsG(carbsG)
                .proteinG(proteinG)
                .build());
    }
}
