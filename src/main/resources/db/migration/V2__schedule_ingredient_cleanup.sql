CREATE EXTENSION IF NOT EXISTS pg_cron;

SELECT cron.schedule(
    'ingredient-cleanup',
    '0 * * * *',
    $$
    UPDATE ingredient
    SET deleted = TRUE
    WHERE deleted = FALSE
      AND ingredient_id NOT IN (
          SELECT MAX(ingredient_id)
          FROM ingredient
          WHERE deleted = FALSE
          GROUP BY chat_id, name
      )
    $$
);
