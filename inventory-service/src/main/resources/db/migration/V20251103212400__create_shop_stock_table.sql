CREATE TABLE shop_stock (
    part_id BIGINT NOT NULL,
    shop_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    PRIMARY KEY (part_id, shop_id),
    FOREIGN KEY (part_id) REFERENCES parts(id)
);