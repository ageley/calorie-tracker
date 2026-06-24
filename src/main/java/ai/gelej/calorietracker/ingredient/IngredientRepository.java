package ai.gelej.calorietracker.ingredient;

import org.springframework.data.repository.CrudRepository;

/**
 * Spring Data JDBC repository for {@link Ingredient} records.
 */
public interface IngredientRepository extends CrudRepository<Ingredient, Long> {
}
