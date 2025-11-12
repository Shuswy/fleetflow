CREATE TABLE repair_shops (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    location VARCHAR(255),
    assigned_warehouse_id BIGINT NOT NULL,
    FOREIGN KEY (assigned_warehouse_id) REFERENCES warehouses(id)
);