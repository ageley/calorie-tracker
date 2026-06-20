package ai.gelej.calorietracker.telegram.webhook;

import ai.gelej.calorietracker.telegram.dispatcher.MessageDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Receives updates pushed by Telegram in webhook (push) mode and dispatches them to the handler
 * chain, which replies on its own. Active only when {@code telegram.bot.mode=webhook}.
 */
@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "telegram.bot", name = "mode", havingValue = "webhook")
public class WebhookController {

    private final MessageDispatcher dispatcher;

    /**
     * Handles a single pushed update by dispatching it to the handler chain.
     *
     * @param update the update pushed by Telegram
     * @return an empty {@code 200 OK} acknowledgement
     */
    @PostMapping(path = "${telegram.bot.webhook.path}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> onUpdate(@RequestBody Update update) {
        dispatcher.dispatch(update);
        return ResponseEntity.ok().build();
    }
}
