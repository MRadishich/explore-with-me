DROP TABLE IF EXISTS categories, users, locations, events, requests, compilations, compilation_events, comments;

CREATE TABLE IF NOT EXISTS categories
(
    id   INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(50) NOT NULL,
    UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name  VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL,
    CONSTRAINT UQ_USER UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS locations
(
    id  INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    lat DOUBLE PRECISION NOT NULL,
    lon DOUBLE PRECISION NOT NULL
);


CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title              VARCHAR(255)                NOT NULL,
    annotation         VARCHAR(2000)               NOT NULL,
    category_id        INT                         NOT NULL,
    created_on         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description        VARCHAR(7000),
    event_date         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id       BIGINT                      NOT NULL,
    location_id        INT                         NOT NULL,
    paid               BOOLEAN DEFAULT FALSE       NOT NULL,
    participant_limit  BIGINT  DEFAULT 0,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN DEFAULT TRUE,
    state              VARCHAR(255)                NOT NULL,
    CONSTRAINT fk_initiator_id FOREIGN KEY (initiator_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_category_id FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE RESTRICT,
    CONSTRAINT fk_locations_id FOREIGN KEY (location_id) REFERENCES locations (id) ON DELETE CASCADE,
    CONSTRAINT UQ_EVENT UNIQUE (initiator_id, title, event_date)
);

CREATE TABLE IF NOT EXISTS requests
(
    id        BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    event_id  BIGINT                   NOT NULL,
    requester BIGINT                   NOT NULL,
    created   TIMESTAMP WITH TIME ZONE NOT NULL,
    status    VARCHAR(20)              NOT NULL,
    CONSTRAINT fk_requests_event_id FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    CONSTRAINT fk_requests_requester FOREIGN KEY (requester) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title  VARCHAR(50) NOT NULL,
    pinned BOOLEAN
);

CREATE TABLE IF NOT EXISTS compilation_events
(
    compilation_id INT    NOT NULL,
    event_id       BIGINT NOT NULL,
    CONSTRAINT pk_compilation_events PRIMARY KEY (compilation_id, event_id),
    CONSTRAINT fk_compilation_events_compilation_id FOREIGN KEY (compilation_id) REFERENCES compilations (id) ON DELETE CASCADE,
    CONSTRAINT fk_compilation_events_event_id FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments
(
    id       BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    event_id BIGINT                      NOT NULL,
    user_id  BIGINT                      NOT NULL,
    created  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated  TIMESTAMP WITHOUT TIME ZONE,
    text     VARCHAR(2000)               NOT NULL,
    CONSTRAINT fk_comments_id_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_compilation_events FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE
)