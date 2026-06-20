package ai.gelej.calorietracker.telegram.longpolling;

import ai.gelej.calorietracker.telegram.BotProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Wiring for long-polling (pull) mode. Active when {@code telegram.bot.mode=long-polling}
 * (the default). Produces a plain {@link TelegramClient} used only to send replies; the
 * Telegram Spring Boot starter does the polling.
 */
@Configuration
@EnableConfigurationProperties(BotProperties.class)
@ConditionalOnProperty(prefix = "telegram.bot", name = "mode", havingValue = "long-polling",
        matchIfMissing = true)
public class LongPollingClientConfig {

    /**
     * Creates the Telegram API client authenticated with the configured bot token.
     *
     * @param properties the bot settings carrying the token
     * @return a client for calling the Telegram Bot API
     */
    @Bean
    public TelegramClient telegramClient(BotProperties properties) {
        return new OkHttpTelegramClient(properties.token());
    }
}
