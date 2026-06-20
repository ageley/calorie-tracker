# AI calory tracker

## Components & integrations

```
 ┌──────────┐        messages        ┌──────────────┐     getUpdates / webhook    ┌─────────────────────┐     save       ┌──────────────┐
 │          │ ─────────────────────► │              │ ──────────────────────────► │                     │ ────────────►  │              │
 │   User   │                        │   Telegram   │                             │   calorie-tracker   │                │   calorie-tracker-pg    │
 │          │ ◄───────────────────── │              │ ◄────────────────────────── │                     │ ◄────────────  │              │
 └──────────┘     confirmations      └──────────────┘     sendMessage (tool)      └─────────┬───────────┘    ingredients └──────────────┘
                                                                                            │ ▲
                                                                                    extract │ │ tools
                                                                                            ▼ │
                                                                                   ┌────────────────────┐
                                                                                   │   Anthropic Haiku   │
                                                                                   └────────────────────┘
```

- **User** — chats with the bot in Telegram, sends ingredient nutrition facts and receives a confirmation.
- **Telegram** — the Bot API platform that relays messages between the user and the bot.
- **calorie-tracker** — this Spring Boot application; it parses ingredient messages and saves them.
- **Postgres** — stores ingredient records (per chat), managed with Flyway and Spring Data JDBC.
- **Anthropic Haiku** — handles messages the programmatic parser cannot, using the same save/send tools.

## Saving an ingredient

Send the bot a product's nutrition facts per 100 grams. The first non-blank line is the ingredient
name; the next four lines are calories, fat, carbs and protein in any order. Names and units may be
written in English or Russian, in any case, with a dash, colon, comma, space or tab between a fact
and its amount; the amount may use a comma or dot decimal separator and the unit may be omitted.

```
Milk chocolate
Calories: 438kcal
Fat: 19g
Carbs: 61g
Protein: 5g
```

The bot saves the record and replies with a confirmation in the message's language. The confirmation
mirrors the input so it can be forwarded and parsed again; lines ending with a sparkles emoji ✨ are
comments and are dropped on re-parsing:

```
Saved ✨
Milk chocolate
Calories: 438 kcal
Fat: 19 g
Carbs: 61 g
Protein: 5 g
```

Messages that cannot be parsed programmatically are handled by the AI agent, which extracts and
saves the ingredient (or asks for the correct format) and replies in the language it was asked in.

## Running locally

Create an .env file in the project root (see [.env.example](.env.example))

Build a .jar from the project root:

```shell
./gradlew build
```

Run an app:

```shell
docker compose up -d --build
```

Cleanup:

```shell
docker compose down -v --rmi all
```

### Switching delivery mode

The mode is controlled by `telegram.bot.mode` in [`application.yml`](src/main/resources/application.yml)

| Mode           | Value          | Notes                                                           |
|----------------|----------------|-----------------------------------------------------------------|
| Long polling   | `long-polling` | Default. The bot pulls updates; no public URL needed.           |
| Webhook (push) | `webhook`      | Telegram pushes updates to `telegram.bot.webhook.url` + `path`. |
