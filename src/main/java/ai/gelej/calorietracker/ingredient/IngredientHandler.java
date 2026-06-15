package ai.gelej.calorietracker.ingredient;

import ai.gelej.calorietracker.ingredient.parsing.IngredientParser;
import ai.gelej.calorietracker.telegram.SendTelegramMessageTool;
import ai.gelej.calorietracker.telegram.dispatcher.handlers.MessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
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
public class IngredientHandler implements MessageHandler {

    private final List<IngredientParser> parsers;
    private final SaveIngredientTool saveIngredientTool;
    private final SendTelegramMessageTool sendTelegramMessageTool;
    private final IngredientConfirmationFormatter formatter;

    @Override
    public boolean handle(Update update) {
        if (update == null || !update.hasMessage()) {
            return false;
        }
        Message message = update.getMessage();
        if (!message.hasText()) {
            return false;
        }
        Long chatId = message.getChatId();
        String text = message.getText();
        for (IngredientParser parser : parsers) {
            Optional<NutritionFacts> parsed = parser.parse(text);
            if (parsed.isPresent()) {
                NutritionFacts facts = parsed.get();
                saveIngredientTool.saveIngredient(chatId, facts.name(), facts.caloriesKcal(),
                        facts.fatG(), facts.carbsG(), facts.proteinG());
                sendTelegramMessageTool.sendMessage(chatId, formatter.format(facts, parser.language()));
                return true;
            }
        }
        return false;
    }
}
