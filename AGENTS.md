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

## Data carriers

- Prefer records with `@Builder(toBuilder = true)` over POJOs for immutable data carriers when framework binding and persistence requirements allow it.
- Write each record component on its own line. Put each component annotation on its own line as well.

## Lombok

- Prefer Lombok over hand-written boilerplate. Use `@RequiredArgsConstructor` for constructor
  injection of `final` fields instead of declaring the constructor by hand; `@Getter`/`@Setter`
  for accessors; `@Slf4j` for loggers.
- Build objects with `@Builder` once a type has more than three constructor parameters, so call
  sites set only the fields they care about by name. Combined with database-owned columns (see
  **Database**), this means never passing throwaway `false`/`null` arguments for values something
  else fills in. Add `toBuilder = true` so callers (especially tests) can derive a variant from an
  existing instance by changing only the fields they care about.

## APIs

- Never call an API that is already marked `@Deprecated`; adopt its documented replacement, even on
  milestone/pre-release dependency versions. Studying the current API is the point.

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
- Never repeat the same test data twice within a test. Build one generic data object in a
  `@BeforeEach`/`@BeforeAll` setup and reuse it across the action and the assertions, deriving
  per-case variants by changing only the fields a case actually exercises (e.g. via
  `@Builder(toBuilder = true)` or by reusing the shared object's unchanged fields).
- Cover behaviour that only exists in the database (migrations, column defaults) with a
  Testcontainers integration test against the real image, rather than mocking it away.

## Build & dependencies

- Keep **all versions** (Java, Gradle plugins, dependencies, BOMs) in `gradle.properties`; no
  version literals in `build.gradle.kts`.
- Reference dependency/BOM versions with Kotlin delegated properties and `$` interpolation:
  `val telegramBotsVersion: String by project` then `"org.telegram:telegrambots-client:$telegramBotsVersion"`.
- Wire **plugin** versions in `settings.gradle.kts`: read them in `pluginManagement` with
  `val springBootVersion: String by settings` and apply with `id("...") version springBootVersion`.
  The `build.gradle.kts` `plugins { }` block then lists plugins without versions.
- Keep dependency versions current; adopt new GA releases promptly and fix whatever they deprecate.

## Database

- Use `snake_case` for all table and column names; table names are plural (e.g. `ingredients`).
- Prefix the primary-key column with the singular table name, e.g. `ingredient_id` rather than a
  bare `id`. Map it in the entity with `@Column("ingredient_id")` while keeping the Java field named
  `id`.
- Let the database own audit/state columns. Give columns such as `created_at` and `deleted` a
  database `DEFAULT` and mark the corresponding entity fields `@ReadOnlyProperty`, so they are
  populated by the database on read and never written from application code.

## Spring wiring

- Prefer plain beans over boilerplate lifecycle wrappers. Declare beans directly as components, not
  wrapped inside a `@Configuration` factory method, unless a factory adds value.
- Select beans by configuration with `@ConditionalOnProperty` rather than `@ConditionalOnExpression`
  when a simple property match suffices.
- Prefer a one-shot startup side effect (e.g. registering an external webhook) in the bean that owns
  it over a `SmartLifecycle` whose teardown is not strictly required.

## Docker

- The application `Dockerfile` contains only `FROM`, `COPY` the jar, and `ENTRYPOINT`. The jar is
  built by the CI/Gradle step beforehand; the image does not build it.
- Pin base images to a minor (e.g. `postgres:18.4-alpine3.24`).
- Favour clarity over micro-optimization: skip space-shaving steps such as
  `rm -rf /var/lib/apt/lists/*` that add cognitive load to save a few megabytes.
