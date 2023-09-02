CREATE TABLE IF NOT EXISTS categories
(
    id   int PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name varchar(50) NOT NULL,
    UNIQUE (name)
);