CREATE TABLE IF NOT EXISTS genetic_sequences (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    type VARCHAR(10) NOT NULL,
    sequence LONGTEXT NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_type ON genetic_sequences(type);
CREATE INDEX idx_name ON genetic_sequences(name);

