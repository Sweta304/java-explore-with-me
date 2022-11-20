DROP TABLE IF EXISTS events, categories, users, compilations, requests;

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    category_name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (id)
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
    annotation VARCHAR(255) NOT NULL,
    category BIGINT REFERENCES categories (id),
    confirmed_requests BIGINT,
    created_on timestamp,
    description VARCHAR(512) NOT NULL,
    event_date timestamp,
    initiator BIGINT REFERENCES users (id),
    lat FLOAT,
    lon FLOAT,
    paid BOOLEAN,
    participant_limit BIGINT,
    published_on VARCHAR(20),
    request_moderation BOOLEAN,
    event_state VARCHAR(20),
    title VARCHAR(255) NOT NULL,
    views BIGINT,
    CONSTRAINT pk_event PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS compilations (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event BIGINT REFERENCES events (id),
    pinned BOOLEAN,
    title VARCHAR(255) NOT NULL,
    CONSTRAINT pk_compilation PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created timestamp,
    event BIGINT REFERENCES events (id),
    requester BIGINT REFERENCES users (id),
    status VARCHAR(255) NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id)
    );