--liquibase formatted sql

--changeset dev:007-create-sea-shipment
CREATE TABLE sea_shipment (
                              id                   BIGSERIAL      PRIMARY KEY,
                              client_id            BIGINT         NOT NULL REFERENCES client(id),
                              product_type_id      BIGINT         NOT NULL REFERENCES product_type(id),
                              port_id              BIGINT         NOT NULL REFERENCES port(id),
                              quantity             INTEGER        NOT NULL CHECK (quantity > 0),
                              registration_date    DATE           NOT NULL DEFAULT CURRENT_DATE,
                              delivery_date        DATE           NOT NULL,
                              tracking_number      CHAR(10)       NOT NULL UNIQUE,
                              fleet_number         VARCHAR(9)     NOT NULL,
                              base_price           NUMERIC(12,2)  NOT NULL CHECK (base_price > 0),
                              final_price          NUMERIC(12,2)  NOT NULL,
                              CONSTRAINT chk_sea_delivery_date CHECK (delivery_date >= registration_date),
                              CONSTRAINT chk_sea_fleet_number  CHECK (fleet_number ~ '^[A-Z]{3}[0-9]{4}[A-Z]{1}$'),
    CONSTRAINT chk_sea_tracking      CHECK (tracking_number ~ '^[A-Z0-9]{10}$')
);

CREATE INDEX idx_sea_shipment_client_id       ON sea_shipment(client_id);
CREATE INDEX idx_sea_shipment_tracking_number ON sea_shipment(tracking_number);

--rollback DROP INDEX idx_sea_shipment_tracking_number; DROP INDEX idx_sea_shipment_client_id; DROP TABLE sea_shipment;