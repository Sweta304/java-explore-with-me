DROP TABLE IF EXISTS events, categories, users, compilations, events_compilations, requests, ratings;

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    category_name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (id),
    CONSTRAINT UQ_CAT_NAME UNIQUE (category_name)
    );

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
    );

CREATE TABLE IF NOT EXISTS events (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation VARCHAR(5000) NOT NULL,
    category BIGINT REFERENCES categories (id),
    created_on timestamp,
    description VARCHAR(5000) NOT NULL,
    event_date timestamp,
    initiator BIGINT REFERENCES users (id),
    lat FLOAT,
    lon FLOAT,
    paid BOOLEAN,
    participant_limit BIGINT,
    published_on timestamp,
    request_moderation BOOLEAN,
    event_state VARCHAR(20),
    title VARCHAR(255) NOT NULL,
    CONSTRAINT pk_event PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS compilations (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    pinned BOOLEAN,
    title VARCHAR(255) NOT NULL,
    CONSTRAINT pk_compilation PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS events_compilations (
    compilation_id BIGINT REFERENCES compilations (id),
    event_id BIGINT REFERENCES events (id),
    PRIMARY KEY (compilation_id, event_id)
    );

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created timestamp,
    event BIGINT REFERENCES events (id),
    requester BIGINT REFERENCES users (id),
    status VARCHAR(255) NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS ratings (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    visitor BIGINT REFERENCES users (id),
    event BIGINT REFERENCES events (id),
    liked BOOLEAN,
    disliked BOOLEAN,
    CONSTRAINT pk_rating PRIMARY KEY (id)
    );