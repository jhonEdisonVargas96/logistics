--liquibase formatted sql

--changeset dev:001-create-product-type
CREATE TABLE product_type (
                              id          BIGSERIAL    PRIMARY KEY,
                              name        VARCHAR(100) NOT NULL,
                              description VARCHAR(255),
                              status      CHAR(1)      NOT NULL DEFAULT 'A',
                              CONSTRAINT chk_product_type_status CHECK (status IN ('A','I'))
);

--rollback DROP TABLE IF EXISTS product_type;