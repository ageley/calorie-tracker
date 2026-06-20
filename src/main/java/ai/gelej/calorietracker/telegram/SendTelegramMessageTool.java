package ai.gelej.calorietracker.telegram;

import ai.gelej.calorietracker.ai.AiToolContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Tool that sends a text message to a Telegram chat. Exposed both to the programmatic handler and to
 * the AI agent so both reply through the same path. The destination chat is taken from the trusted
 * {@link ToolContext}, never from a model-supplied parameter.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SendTelegramMessageTool {

    private final TelegramClient telegramClient;

    /**
     * Sends a text message to the chat bound in the tool context.
     *
     * @param text the message text
     * @param toolContext the tool context carrying the trusted chat id
     */
    @Tool(description = "Send a text message to the current Telegram chat")
    public void sendMessage(
            @ToolParam(description = "Message text") String text,
            ToolContext toolContext) {
        send((Long) toolContext.getContext().get(AiToolContext.CHAT_ID), text);
    }

    /**
     * Sends a text message to the given chat. Used by the programmatic handler, which already knows
     * the trusted chat id.
     *
     * @param chatId the Telegram chat to send the message to
     * @param text the message text
     */
    public void send(Long chatId, String text) {
        try {
            telegramClient.execute(SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text(text)
                    .build());
        } catch (TelegramApiException e) {
            log.error("Failed to send message to chat {}", chatId, e);
        }
    }
}
