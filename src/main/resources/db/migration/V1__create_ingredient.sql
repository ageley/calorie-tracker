CREATE TABLE ingredient (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    chat_id       BIGINT      NOT NULL,
    name          TEXT        NOT NULL,
    calories_kcal NUMERIC      NOT NULL,
    fat_g         NUMERIC      NOT NULL,
    carbs_g       NUMERIC      NOT NULL,
    protein_g     NUMERIC      NOT NULL,
    deleted       BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_ingredient_chat_id ON ingredient (chat_id);
