CREATE TABLE IF NOT EXISTS categories
(
    id   int PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name varchar(50) NOT NULL,
    UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS users
(
    id    int PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name  varchar(250) NOT NULL,
    email varchar(254) NOT NULL,
    UNIQUE (email)
);