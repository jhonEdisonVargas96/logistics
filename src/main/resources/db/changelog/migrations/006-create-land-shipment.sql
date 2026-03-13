--liquibase formatted sql

--changeset dev:006-create-land-shipment
CREATE TABLE land_shipment (
                               id                   BIGSERIAL      PRIMARY KEY,
                               client_id            BIGINT         NOT NULL REFERENCES client(id),
                               product_type_id      BIGINT         NOT NULL REFERENCES product_type(id),
                               warehouse_id         BIGINT         NOT NULL REFERENCES warehouse(id),
                               quantity             INTEGER        NOT NULL CHECK (quantity > 0),
                               registration_date    DATE           NOT NULL DEFAULT CURRENT_DATE,
                               delivery_date        DATE           NOT NULL,
                               tracking_number      CHAR(10)       NOT NULL UNIQUE,
                               vehicle_plate        VARCHAR(7)     NOT NULL,
                               base_price           NUMERIC(12,2)  NOT NULL CHECK (base_price > 0),
                               final_price          NUMERIC(12,2)  NOT NULL,
                               CONSTRAINT chk_land_delivery_date  CHECK (delivery_date >= registration_date),
                               CONSTRAINT chk_land_vehicle_plate  CHECK (vehicle_plate ~ '^[A-Z]{3}[0-9]{3}$'),
    CONSTRAINT chk_land_tracking       CHECK (tracking_number ~ '^[A-Z0-9]{10}$')
);

CREATE INDEX idx_land_shipment_client_id      ON land_shipment(client_id);
CREATE INDEX idx_land_shipment_tracking_number ON land_shipment(tracking_number);

--rollback DROP INDEX idx_land_shipment_tracking_number; DROP INDEX idx_land_shipment_client_id; DROP TABLE land_shipment;