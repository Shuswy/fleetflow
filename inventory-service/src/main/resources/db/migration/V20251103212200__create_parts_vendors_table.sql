CREATE TABLE parts_vendors (
    part_id BIGINT NOT NULL,
    vendor_id BIGINT NOT NULL,
    cost DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (part_id, vendor_id),
    FOREIGN KEY (part_id) REFERENCES parts(id),
    FOREIGN KEY (vendor_id) REFERENCES vendors(id)
);