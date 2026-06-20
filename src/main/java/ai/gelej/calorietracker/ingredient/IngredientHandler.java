package ai.gelej.calorietracker.ingredient;

import ai.gelej.calorietracker.ingredient.parsing.IngredientParser;
import ai.gelej.calorietracker.telegram.SendTelegramMessageTool;
import ai.gelej.calorietracker.telegram.dispatcher.handlers.AbstractTextMessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.List;
import java.util.Optional;

/**
 * Programmatic ingredient handler. Tries each language parser in turn; on the first successful parse
 * it saves the ingredient and replies with a confirmation in the message's language, both through
 * shared tools. Returns {@code false} when no parser matches, deferring to the AI handler.
 */
@Component
@Order(0)
@RequiredArgsConstructor
public class IngredientHandler extends AbstractTextMessageHandler {

    private final List<IngredientParser> parsers;
    private final SaveIngredientTool saveIngredientTool;
    private final SendTelegramMessageTool sendTelegramMessageTool;
    private final List<IngredientConfirmationFormatter> formatters;

    @Override
    protected boolean handle(Message message, List<String> lines) {
        Long chatId = message.getChatId();
        for (IngredientParser parser : parsers) {
            Optional<NutritionFacts> parsed = parser.parse(lines);
            if (parsed.isPresent()) {
                NutritionFacts facts = parsed.get();
                saveIngredientTool.save(chatId, facts.name(), facts.caloriesKcal(),
                        facts.fatG(), facts.carbsG(), facts.proteinG());
                sendTelegramMessageTool.send(chatId, format(facts, parser.language()));
                return true;
            }
        }
        return false;
    }

    private String format(NutritionFacts facts, Language language) {
        return formatters.stream()
                .filter(formatter -> formatter.language() == language)
                .findFirst()
                .orElseThrow()
                .format(facts);
    }
}
