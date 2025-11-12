CREATE TABLE request_line_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL,
    part_id BIGINT NOT NULL,
    quantity_requested INT NOT NULL,
    FOREIGN KEY (request_id) REFERENCES mechanic_requests(id)
);