CREATE EXTENSION IF NOT EXISTS pg_cron;

SELECT cron.schedule(
    'ingredient-cleanup',
    '0 * * * *',
    $$
    UPDATE ingredients
    SET deleted = TRUE
    WHERE deleted = FALSE
      AND ingredient_id NOT IN (
          SELECT MAX(ingredient_id)
          FROM ingredients
          WHERE deleted = FALSE
          GROUP BY chat_id, name
      )
    $$
);
