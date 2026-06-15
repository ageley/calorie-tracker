package ai.gelej.calorietracker.ingredient;

import org.springframework.data.repository.CrudRepository;

/**
 * Spring Data JDBC repository for {@link Ingredient} records. Cleanup of superseded duplicates is
 * handled in the database by a pg_cron schedule, not here.
 */
public interface IngredientRepository extends CrudRepository<Ingredient, Long> {
}
