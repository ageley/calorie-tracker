package ai.gelej.calorietracker.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Tool that sends a text message to a Telegram chat. Exposed both to the programmatic handler and to
 * the AI agent so both reply through the same path.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SendTelegramMessageTool {

    private final TelegramClient telegramClient;

    /**
     * Sends a text message to the given chat.
     *
     * @param chatId the Telegram chat to send the message to
     * @param text the message text
     */
    @Tool(description = "Send a text message to a Telegram chat")
    public void sendMessage(
            @ToolParam(description = "Telegram chat id to send the message to") Long chatId,
            @ToolParam(description = "Message text") String text) {
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
