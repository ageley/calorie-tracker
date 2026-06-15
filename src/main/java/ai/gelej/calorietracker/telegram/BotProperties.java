package ai.gelej.calorietracker.telegram;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Bot settings bound from the {@code telegram.bot.*} section of {@code application.yml}.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "telegram.bot")
public class BotProperties {

    private String token;

    private Webhook webhook = new Webhook();

    @Getter
    @Setter
    public static class Webhook {

        private String url;

        private String path = "/telegram/webhook";
    }
}
