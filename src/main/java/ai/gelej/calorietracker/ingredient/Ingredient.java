package ai.gelej.calorietracker.ingredient;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * A stored ingredient record scoped to a Telegram chat, holding its nutrition facts per 100 grams
 * in the canonical Calories/Fat/Carbs/Protein order. Duplicates are inserted as-is; superseded rows
 * are flagged with {@code deleted} by a cleanup job rather than removed.
 *
 * @param id the generated identifier
 * @param chatId the Telegram chat the record belongs to
 * @param name the ingredient name
 * @param caloriesKcal energy in kilocalories
 * @param fatG fat in grams
 * @param carbsG carbohydrates in grams
 * @param proteinG protein in grams
 * @param deleted whether the row has been superseded by a newer duplicate
 * @param createdAt the instant the row was stored
 */
@Table("ingredient")
public record Ingredient(
        @Id Long id,
        Long chatId,
        String name,
        BigDecimal caloriesKcal,
        BigDecimal fatG,
        BigDecimal carbsG,
        BigDecimal proteinG,
        boolean deleted,
        Instant createdAt) {
}
