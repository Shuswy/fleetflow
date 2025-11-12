CREATE TABLE parts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    part_number VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    buffer_level INT NOT NULL DEFAULT 10
);