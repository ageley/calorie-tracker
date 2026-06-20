package ai.gelej.calorietracker.telegram.dispatcher;

import ai.gelej.calorietracker.telegram.dispatcher.handlers.MessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * Routes an incoming update through the ordered chain of {@link MessageHandler}s, stopping at the
 * first handler that handles it. Each handler replies on its own; the dispatcher only decides who
 * gets the update. The chain is injected in {@code @Order} sequence.
 */
@Service
@RequiredArgsConstructor
public class MessageDispatcher {

    private final List<MessageHandler> handlers;

    /**
     * Dispatches an update to the first handler that accepts it.
     *
     * @param update the incoming Telegram update
     */
    public void dispatch(Update update) {
        for (MessageHandler handler : handlers) {
            if (handler.handle(update)) {
                return;
            }
        }
    }
}
