package ai.gelej.calorietracker.telegram.longpolling;

import ai.gelej.calorietracker.telegram.BotProperties;
import ai.gelej.calorietracker.telegram.dispatcher.MessageDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Long polling (pull) bot. Registered automatically by the Telegram Spring Boot starter, which
 * polls Telegram and feeds updates to this consumer; handlers reply on their own.
 * Active when {@code telegram.bot.mode=long-polling} (the default).
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "telegram.bot", name = "mode", havingValue = "long-polling",
        matchIfMissing = true)
public class LongPollingConsumer implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final BotProperties properties;
    private final MessageDispatcher dispatcher;

    @Override
    public String getBotToken() {
        return properties.token();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    /**
     * Dispatches an incoming update to the handler chain.
     *
     * @param update the update received from Telegram
     */
    @Override
    public void consume(Update update) {
        dispatcher.dispatch(update);
    }
}
