package ai.gelej.calorietracker.telegram.dispatcher.handlers;

import org.jspecify.annotations.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

/**
 * Base handler that applies the applicability checks common to every handler: it accepts only
 * updates that carry a text message and hands the extracted {@link Message} to subclasses.
 */
public abstract class AbstractMessageHandler implements MessageHandler {

    @Override
    public final boolean handle(@Nullable Update update) {
        if (update == null || !update.hasMessage()) {
            return false;
        }
        Message message = update.getMessage();
        if (!message.hasText()) {
            return false;
        }
        return handle(message);
    }

    /**
     * Handles a text message that has already passed the common applicability checks.
     *
     * @param message the incoming text message
     * @return {@code true} if this handler handled the message, {@code false} to defer to the next
     * handler in the chain
     */
    protected abstract boolean handle(Message message);
}
