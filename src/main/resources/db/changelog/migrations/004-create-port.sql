--liquibase formatted sql

--changeset dev:004-create-port
-- port_type: N = national, I = international
CREATE TABLE port (
                      id        BIGSERIAL    PRIMARY KEY,
                      name      VARCHAR(150) NOT NULL,
                      city      VARCHAR(100) NOT NULL,
                      country   VARCHAR(100) NOT NULL,
                      port_type CHAR(1)      NOT NULL CHECK (port_type IN ('N','I')),
                      status    CHAR(1)      NOT NULL DEFAULT 'A',
                      CONSTRAINT chk_port_status CHECK (status IN ('A','I'))
);

--rollback DROP TABLE port;