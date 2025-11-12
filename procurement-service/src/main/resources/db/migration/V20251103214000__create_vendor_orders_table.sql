CREATE TABLE vendor_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    warehouse_id BIGINT NOT NULL,
    logistician_id BIGINT NOT NULL,
    vendor_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_price DECIMAL(12, 2),
    estimated_delivery DATE,
    created_at TIMESTAMP NOT NULL
);