package ai.gelej.calorietracker.ingredient;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Spring Data JDBC repository for {@link Ingredient} records.
 */
public interface IngredientRepository extends CrudRepository<Ingredient, Long> {

    /**
     * Flags every superseded duplicate as deleted, keeping only the newest live row per chat and
     * name.
     *
     * @return the number of rows flagged
     */
    @Modifying
    @Query("""
            UPDATE ingredient
            SET deleted = TRUE
            WHERE deleted = FALSE
              AND id NOT IN (
                  SELECT MAX(id)
                  FROM ingredient
                  WHERE deleted = FALSE
                  GROUP BY chat_id, name
              )
            """)
    int markSupersededDuplicatesDeleted();
}
