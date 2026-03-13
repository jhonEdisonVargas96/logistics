--liquibase formatted sql

--changeset dev:003-create-warehouse
CREATE TABLE warehouse (
                           id      BIGSERIAL    PRIMARY KEY,
                           name    VARCHAR(150) NOT NULL,
                           address VARCHAR(255) NOT NULL,
                           city    VARCHAR(100) NOT NULL,
                           status  CHAR(1)      NOT NULL DEFAULT 'A',
                           CONSTRAINT chk_warehouse_status CHECK (status IN ('A','I'))
);

--rollback DROP TABLE warehouse;