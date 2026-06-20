package ai.gelej.calorietracker.telegram.webhook;

import ai.gelej.calorietracker.telegram.BotProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Wiring for webhook (push) mode. Active when {@code telegram.bot.mode=webhook}. Produces a
 * {@link TelegramClient} that registers the webhook URL with Telegram as it is created, so updates
 * are pushed to {@link WebhookController}.
 */
@Configuration
@EnableConfigurationProperties(BotProperties.class)
@ConditionalOnProperty(prefix = "telegram.bot", name = "mode", havingValue = "webhook")
public class WebhookClientConfig {

    /**
     * Creates the Telegram API client and registers the webhook URL with Telegram. {@code SetWebhook}
     * is idempotent, so it overwrites any previous registration; there is no matching teardown,
     * because a stale webhook is harmless until the bot switches to long polling.
     *
     * @param properties the bot settings carrying the token and webhook URL
     * @return a client for calling the Telegram Bot API, with the webhook already registered
     */
    @Bean
    public TelegramClient telegramClient(BotProperties properties) {
        TelegramClient telegramClient = new OkHttpTelegramClient(properties.token());
        String url = properties.webhook().url() + properties.webhook().path();
        try {
            telegramClient.execute(SetWebhook.builder()
                    .url(url)
                    .build());
        } catch (TelegramApiException e) {
            throw new IllegalStateException("Failed to register Telegram webhook at " + url, e);
        }
        return telegramClient;
    }
}
