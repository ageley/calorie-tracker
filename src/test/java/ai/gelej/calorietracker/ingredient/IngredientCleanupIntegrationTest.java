package ai.gelej.calorietracker.ingredient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the pg_cron cleanup job registered by {@code V2__schedule_ingredient_cleanup.sql} against a
 * real Postgres built from {@code docker/postgres/Dockerfile} with the pg_cron extension. Flyway runs
 * the migrations on the container, then the test runs the job's own stored command on demand (instead
 * of waiting for its hourly schedule) and asserts that superseded duplicates are flagged deleted while
 * the latest row per chat and name survives.
 */
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class IngredientCleanupIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            new ImageFromDockerfile().withDockerfile(Path.of("docker/postgres/Dockerfile")))
            .withDatabaseName("calorie_tracker")
            .withCommand("postgres", "-c", "shared_preload_libraries=pg_cron", "-c",
                    "cron.database_name=calorie_tracker");

    @Autowired
    private IngredientRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void cleanupJob_supersededDuplicates_areFlaggedDeleted() {
        //given
        Ingredient older = save(100L, "Milk chocolate");
        Ingredient newer = save(100L, "Milk chocolate");
        Ingredient other = save(100L, "Dark chocolate");

        //when
        runCleanupJob();

        //then
        assertThat(deleted(older.getId())).isTrue();
        assertThat(deleted(newer.getId())).isFalse();
        assertThat(deleted(other.getId())).isFalse();
    }

    private Ingredient save(long chatId, String name) {
        return repository.save(Ingredient.builder()
                .chatId(chatId)
                .name(name)
                .caloriesKcal(new BigDecimal("438"))
                .fatG(new BigDecimal("19"))
                .carbsG(new BigDecimal("61"))
                .proteinG(new BigDecimal("5"))
                .build());
    }

    private void runCleanupJob() {
        String command = jdbcTemplate.queryForObject(
                "SELECT command FROM cron.job WHERE jobname = 'ingredient-cleanup'", String.class);
        jdbcTemplate.execute(command);
    }

    private boolean deleted(Long id) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                "SELECT deleted FROM ingredients WHERE ingredient_id = ?", Boolean.class, id));
    }
}
