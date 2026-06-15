package ai.gelej.calorietracker.telegram.dispatcher.handlers;

import org.jspecify.annotations.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.List;

/**
 * Base handler for text messages. Applies the applicability checks common to every handler — it
 * accepts only updates that carry a text message — and hands subclasses the message together with its
 * content lines, already stripped of blank lines and of comment lines ending in a sparkles emoji, so
 * every text-parsing handler shares the same normalization.
 */
public abstract class AbstractTextMessageHandler implements MessageHandler {

    private static final String SPARKLES = "✨";

    @Override
    public final boolean handle(@Nullable Update update) {
        if (update == null || !update.hasMessage()) {
            return false;
        }
        Message message = update.getMessage();
        if (!message.hasText()) {
            return false;
        }
        return handle(message, contentLines(message.getText()));
    }

    /**
     * Handles a text message that has already passed the common applicability checks.
     *
     * @param message the incoming text message
     * @param lines the message text split into content lines, stripped of blank and comment lines
     * @return {@code true} if this handler handled the message, {@code false} to defer to the next
     * handler in the chain
     */
    protected abstract boolean handle(Message message, List<String> lines);

    /**
     * Splits message text into content lines, dropping blank lines and comment lines that end with a
     * sparkles emoji.
     *
     * @param text the raw message text
     * @return the stripped content lines, in order
     */
    public static List<String> contentLines(String text) {
        return text.lines()
                .map(String::strip)
                .filter(line -> !line.isEmpty())
                .filter(line -> !line.endsWith(SPARKLES))
                .toList();
    }
}
