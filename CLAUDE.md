# Coding preferences

This file accumulates **general coding preferences** only. Update it after every review round.
Project-specific decisions belong in the Javadoc of the code they concern, not here.

## Comments

- No inline comments and no commented-out code in any file (sources, config, YAML).
- Allowed: Javadoc on classes and public methods only. Keep it brief — state the purpose plus
  input/output parameters where applicable.

## Formatting

- Fluent / builder APIs: keep the first invocation on the same line as the receiver, then put each
  subsequent call on its own line.

  ```java
  telegramClient.execute(SetWebhook.builder()
          .url(url)
          .build());
  ```

## Lombok

- Prefer Lombok over hand-written boilerplate. Use `@RequiredArgsConstructor` for constructor
  injection of `final` fields instead of declaring the constructor by hand; `@Getter`/`@Setter`
  for accessors; `@Slf4j` for loggers.

## Package layout

- Organize by feature first. Group a feature into its own package (e.g. `telegram`); when it has
  several independent variants, give each its own sub-package (e.g. `telegram.longpolling`,
  `telegram.webhook`) and keep shared types in the parent. Code shared across features can still be
  grouped by layer (`config`, `model`, `controller`, `service`).

## Testing

- Name test methods in three underscore-separated parts: the method under test, the test-case
  description, and the expected outcome (e.g. `handle_messageWithText_echoesItBack`).
- Structure the body with `//given`, `//when`, and `//then` sections, each introduced by that
  single-line comment.

## Build & dependencies

- Keep **all versions** (Java, Gradle plugins, dependencies, BOMs) in `gradle.properties`; no
  version literals in `build.gradle.kts`.
- Reference dependency/BOM versions with Kotlin delegated properties and `$` interpolation:
  `val telegramBotsVersion: String by project` then `"org.telegram:telegrambots-client:$telegramBotsVersion"`.
- Wire **plugin** versions in `settings.gradle.kts`: read them in `pluginManagement` with
  `val springBootVersion: String by settings` and apply with `id("...") version springBootVersion`.
  The `build.gradle.kts` `plugins { }` block then lists plugins without versions.
- Keep dependency versions current.

## Database

- Use `snake_case` for all table and column names; table names are plural (e.g. `ingredients`).
- Prefix the primary-key column with the singular table name, e.g. `ingredient_id` rather than a
  bare `id`. Map it in the entity with `@Column("ingredient_id")` while keeping the Java field named
  `id`.
- Let the database own audit/state columns. Give columns such as `created_at` and `deleted` a
  database `DEFAULT` and mark the corresponding entity fields `@ReadOnlyProperty`, so they are
  populated by the database on read and never written from application code.
- Prefer scheduling recurring database maintenance in the database itself (a `pg_cron` job created
  in a Flyway migration) over an application-level `@Scheduled` bean.

## Spring wiring

- Prefer plain beans over boilerplate lifecycle wrappers. Declare beans directly as components, not
  wrapped inside a `@Configuration` factory method, unless a factory adds value.
- Select beans by configuration with `@ConditionalOnProperty` rather than `@ConditionalOnExpression`
  when a simple property match suffices.
- Prefer a one-shot startup side effect (e.g. registering an external webhook) in the bean that owns
  it over a `SmartLifecycle` whose teardown is not strictly required.

## Docker

- The `Dockerfile` contains only `FROM`, `COPY` the jar, and `ENTRYPOINT`. The jar is built by the
  CI/Gradle step beforehand; the image does not build it.
