package ai.gelej.calorietracker.ingredient;

import lombok.Builder;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * A stored ingredient record scoped to a Telegram chat, holding its nutrition facts per 100 grams
 * in the canonical Calories/Fat/Carbs/Protein order. Duplicates are inserted as-is; superseded rows
 * are flagged with {@code deleted} by a scheduled database cleanup rather than removed. Both
 * {@code deleted} and {@code createdAt} are owned by the database (column defaults) and marked
 * {@link ReadOnlyProperty}, so they are populated on read and never written from code. Built through
 * the Lombok {@code @Builder}, so callers set only the columns they own and skip the rest.
 */
@Table("ingredients")
@Getter
@Builder
public class Ingredient {

    @Id
    @Column("ingredient_id")
    @Nullable
    private final Long id;

    private final Long chatId;
    private final String name;
    private final BigDecimal caloriesKcal;
    private final BigDecimal fatG;
    private final BigDecimal carbsG;
    private final BigDecimal proteinG;

    @ReadOnlyProperty
    private final boolean deleted;

    @ReadOnlyProperty
    @Nullable
    private final Instant createdAt;
}
