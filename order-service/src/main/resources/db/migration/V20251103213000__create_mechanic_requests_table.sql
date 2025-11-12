CREATE TABLE mechanic_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    repair_shop_id BIGINT NOT NULL,
    mechanic_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    assigned_warehouse_id BIGINT,
    created_at TIMESTAMP NOT NULL,
    last_updated_at TIMESTAMP NOT NULL
);