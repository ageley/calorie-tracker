package ai.gelej.calorietracker.telegram;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Bot settings bound from the {@code telegram.bot.*} section of {@code application.yml}.
 */
@ConfigurationProperties(prefix = "telegram.bot")
public record BotProperties(
        String token,
        @DefaultValue
        Webhook webhook
) {

    /**
     * Webhook endpoint settings.
     *
     * @param url the public base URL
     * @param path the webhook path
     */
    public record Webhook(
            String url,
            @DefaultValue("/telegram/webhook")
            String path
    ) {
    }
}
