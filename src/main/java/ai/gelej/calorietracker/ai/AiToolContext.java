package ai.gelej.calorietracker.ai;

/**
 * Keys for values bound out-of-band into the Spring AI tool context. Trusted data such as the
 * Telegram chat id is passed this way so tools read it from the context instead of accepting it as a
 * model-visible parameter, preventing prompt injection from redirecting writes or messages.
 */
public final class AiToolContext {

    /**
     * Context key holding the trusted Telegram chat id the current message belongs to.
     */
    public static final String CHAT_ID = "chatId";

    private AiToolContext() {
    }
}
