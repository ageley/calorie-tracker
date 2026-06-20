package ai.gelej.calorietracker.ingredient;

import lombok.Builder;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * A stored ingredient record scoped to a Telegram chat, holding its nutrition facts per 100 grams
 * in the canonical Calories/Fat/Carbs/Protein order. Duplicates are inserted as-is. Both {@code deleted} and {@code createdAt} are owned
 * by the database (column defaults) and marked {@link ReadOnlyProperty}, so they are populated on
 * read and never written from code. Built through the Lombok {@code @Builder}, so callers set only
 * the columns they own and skip the rest; {@code toBuilder} lets tests derive a variant from an
 * existing instance by changing only the fields they care about.
 */
@Table("ingredients")
@Builder(toBuilder = true)
public record Ingredient(
        @Id
        @Column("ingredient_id")
        @Nullable
        Long id,
        Long chatId,
        String name,
        BigDecimal caloriesKcal,
        BigDecimal fatG,
        BigDecimal carbsG,
        BigDecimal proteinG,
        @ReadOnlyProperty
        boolean deleted,
        @ReadOnlyProperty
        @Nullable
        Instant createdAt) {
}
