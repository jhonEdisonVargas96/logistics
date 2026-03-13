--liquibase formatted sql

--changeset dev:005-create-user
CREATE TABLE app_user (
                          id             BIGSERIAL    PRIMARY KEY,
                          username       VARCHAR(100) NOT NULL,
                          email          VARCHAR(150) NOT NULL UNIQUE,
                          password_hash  VARCHAR(255) NOT NULL,
                          role           VARCHAR(50)  NOT NULL DEFAULT 'USER',
                          status         CHAR(1)      NOT NULL DEFAULT 'A',
                          created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
                          CONSTRAINT chk_user_status CHECK (status IN ('A','I'))
);

--rollback DROP TABLE app_user;