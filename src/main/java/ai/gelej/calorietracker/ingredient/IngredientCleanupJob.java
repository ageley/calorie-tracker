package ai.gelej.calorietracker.ingredient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Periodically flags superseded duplicate ingredients as deleted, keeping only the newest row per
 * chat and name. The schedule is configured by {@code ingredient.cleanup.cron}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IngredientCleanupJob {

    private final IngredientRepository repository;

    /**
     * Runs the duplicate cleanup on the configured schedule.
     */
    @Scheduled(cron = "${ingredient.cleanup.cron}")
    public void markSupersededDuplicatesDeleted() {
        int flagged = repository.markSupersededDuplicatesDeleted();
        log.info("Flagged {} superseded duplicate ingredients as deleted", flagged);
    }
}
