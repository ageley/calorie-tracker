# Pull request review notes

## Record formatting rule

I added the requested record-formatting preference to `AGENTS.md`: each record component should be written on its own line, and each component annotation should be written on its own line as well. This makes the `BotProperties` formatting decision reusable for future reviews instead of leaving it as a one-off fix.

## GitHub Actions image publishing on pull requests

The original problem was that the workflow runs for both `pull_request` and `push` events, but publishing a Docker image to GitHub Container Registry needs a token that can write packages. Pull requests from forks and Dependabot commonly receive a restricted `GITHUB_TOKEN`, so a workflow that tries to log in and push an image during a pull request can fail even if tests and the jar build pass.

The current workflow avoids that by still building and testing on pull requests, while logging in to GHCR only on `push` and setting the Docker action `push` input to true only when `github.event_name == 'push'`. On pull requests, the Docker action can build the image without publishing it.

## When the pull request events fire

The workflow is configured with these pull request activity types:

- `opened`: fires when a pull request is first opened.
- `synchronize`: fires when the pull request branch is updated with new commits, which is the usual "PR update" event.

Because the workflow lists only those two activity types under `pull_request`, it does not run for other pull request activity such as edited descriptions, labels, review requests, or reopening. It also still runs on `push` to `main` so merged changes can publish the snapshot image.

## Gradle command for a single variable

Yes. The branch adds a `printAppVersion` Gradle task, so CI can read only the application version with:

```bash
./gradlew -q printAppVersion
```

That is cleaner than parsing `gradle.properties` in shell because Gradle remains the source of truth for how the project exposes the value.

## Deleted Flyway version 2 migration

The automated Flyway warning is valid for projects where a versioned migration has already been applied to at least one database. Flyway records applied migrations in `flyway_schema_history`; if a database contains a row for version `2` but the code no longer contains `V2__...sql`, Flyway validation can fail at startup because the applied migration is missing from the resolved local migrations.

In this pull request, you clarified that this migration was not applied anywhere. Under that assumption, deleting it is safe because no existing database has a `flyway_schema_history` row that needs the deleted file to remain resolvable. If that assumption changes, the safer path is to restore the old `V2__schedule_ingredient_cleanup.sql` file and add a new forward migration that removes the scheduled behavior.

## Split CI workflows

The CI configuration is now split into two independent workflows.

The pull request workflow runs only for these pull request events:

- `opened`: a pull request is created.
- `synchronize`: the pull request source branch receives new commits.

That workflow only runs unit tests with `./gradlew test`; it does not build, log in to GHCR, push an image, or create a Git tag.

The main release workflow runs only when `main` receives a pushed commit. It runs unit tests, builds the jar, reads the version with `./gradlew -q printAppVersion`, builds and pushes `ghcr.io/<owner>/<repo>:<appVersion>`, and tags the pushed `main` commit with exactly `<appVersion>`.

## Setting up free GHCR publishing for a public GitHub repository

GitHub Container Registry is available for public repositories without creating a separate Docker Hub repository. The workflow can publish to `ghcr.io/<owner>/<repo>` by using the repository's built-in `GITHUB_TOKEN`.

To set it up:

1. Open the GitHub repository settings.
2. Go to `Actions` > `General`.
3. In `Workflow permissions`, select `Read and write permissions`.
4. Save the setting.
5. Merge the workflow into `main` and let the main release workflow run once.
6. Open the repository or organization `Packages` page and find the created container package.
7. If needed, open the package settings and connect it to the repository.
8. For a public image, set the package visibility to public.

No Docker Hub account or paid Docker repository is required for this setup. The image name produced by the workflow uses the GitHub repository name and the app version only, for example `ghcr.io/ageley/calorie-tracker:0.0.1`.

## Workflow action simplification

The main release workflow now uses `docker/metadata-action` to prepare Docker image tags and labels from the Gradle-provided application version. That keeps the workflow from manually building the image name in shell.

The workflow also uses `rickstaa/action-create-tag` to create the Git tag for the pushed `main` commit. The tag value still comes directly from `./gradlew -q printAppVersion`, so the Docker image tag and Git tag stay identical.
