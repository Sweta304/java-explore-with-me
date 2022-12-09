DROP TABLE IF EXISTS endpoint_hit;

CREATE TABLE IF NOT EXISTS endpoint_hit (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app VARCHAR(50),
    uri VARCHAR(512),
    ip VARCHAR(50),
    timestamp timestamp,
    CONSTRAINT pk_endpoint PRIMARY KEY (id)
    );