CREATE TABLE IF NOT EXISTS hits
(
    id        bigint GENERATED ALWAYS AS IDENTITY,
    app       varchar(255) NOT NULL,
    uri       varchar(255) NOT NULL,
    ip        varchar(50)  NOT NULL,
    timestamp timestamp    NOT NULL
);