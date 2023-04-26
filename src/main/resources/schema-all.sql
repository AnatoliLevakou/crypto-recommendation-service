DROP TABLE crypto IF EXISTS;

CREATE TABLE crypto  (
    crypto_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    timestamp VARCHAR(20),
    symbol VARCHAR(20),
    price NUMERIC(16, 4)
);

DROP TABLE configuration IF EXISTS;

CREATE TABLE configuration  (
    configuration_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(20),
    description VARCHAR(500),
    data VARCHAR(500)
);