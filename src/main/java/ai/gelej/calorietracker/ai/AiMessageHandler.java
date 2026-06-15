package ai.gelej.calorietracker.ai;

import ai.gelej.calorietracker.ingredient.SaveIngredientTool;
import ai.gelej.calorietracker.telegram.SendTelegramMessageTool;
import ai.gelej.calorietracker.telegram.dispatcher.handlers.MessageHandler;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

/**
 * Fallback handler backed by the cheapest Haiku model. It runs after the programmatic parser and
 * handles whatever could not be parsed: it tries to extract and save the ingredient through the
 * shared tools and replies in the user's own language, always through the Telegram tool.
 */
@Component
@Order(1)
public class AiMessageHandler implements MessageHandler {

    private static final String SYSTEM_PROMPT = """
            You are a nutrition assistant for a Telegram bot that stores ingredient nutrition facts \
            per 100 grams in the canonical Calories/Fat/Carbs/Protein order.
            Each user message begins with a line "chatId: <id>" identifying the Telegram chat; the \
            rest is the user's text.
            Try to extract the ingredient name and its calories (kcal), fat (g), carbs (g) and \
            protein (g) per 100 grams. If you can, call the saveIngredient tool with that chatId and \
            the values, then call the sendMessage tool to confirm to that chatId. If you cannot \
            extract all four facts, call the sendMessage tool asking the user for the correct format.
            Always reply by calling the sendMessage tool; never answer with plain text only.
            Always reply in the same language the user wrote in.
            Every explanatory or comment line you put in a message must end with a sparkles emoji ✨, \
            so it can be safely removed when the message is parsed again.""";

    private final ChatClient chatClient;

    public AiMessageHandler(ChatClient.Builder chatClientBuilder, SaveIngredientTool saveIngredientTool,
                            SendTelegramMessageTool sendTelegramMessageTool) {
        this.chatClient = chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultTools(saveIngredientTool, sendTelegramMessageTool)
                .build();
    }

    @Override
    public boolean handle(Update update) {
        if (update == null || !update.hasMessage()) {
            return false;
        }
        Message message = update.getMessage();
        if (!message.hasText()) {
            return false;
        }
        chatClient.prompt()
                .user("chatId: " + message.getChatId() + "\n" + message.getText())
                .call()
                .content();
        return true;
    }
}
