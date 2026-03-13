--liquibase formatted sql

--changeset dev:002-create-client
CREATE TABLE client (
                        id      BIGSERIAL    PRIMARY KEY,
                        name    VARCHAR(150) NOT NULL,
                        email   VARCHAR(150) NOT NULL UNIQUE,
                        phone   VARCHAR(20),
                        status  CHAR(1)      NOT NULL DEFAULT 'A',
                        CONSTRAINT chk_client_status CHECK (status IN ('A','I'))
);

--rollback DROP TABLE client;