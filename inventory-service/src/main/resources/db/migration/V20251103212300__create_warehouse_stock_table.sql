CREATE TABLE warehouse_stock (
    part_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    PRIMARY KEY (part_id, warehouse_id),
    FOREIGN KEY (part_id) REFERENCES parts(id)
);