package ai.gelej.calorietracker.telegram.dispatcher.handlers;

import org.jspecify.annotations.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * A single strategy for handling an incoming update. Each handler encapsulates its own applicability
 * checks and performs any reply itself through a tool; a new handler can be added to the chain
 * without changing others.
 */
public interface MessageHandler {

    /**
     * Attempts to handle the given update.
     *
     * @param update the incoming Telegram update
     * @return {@code true} if this handler handled the update, {@code false} to defer to the next
     * handler in the chain
     */
    boolean handle(@Nullable Update update);
}
