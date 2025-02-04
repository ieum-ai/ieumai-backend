CREATE DATABASE auth_db;

USE auth_db;

CREATE TABLE verification_pins (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   email VARCHAR(255) NOT NULL,
                                   pin VARCHAR(6) NOT NULL,
                                   attempts INT NOT NULL DEFAULT 0,
                                   created_at DATETIME NOT NULL,
                                   expires_at DATETIME NOT NULL,
                                   INDEX idx_email_expires (email, expires_at)
);

CREATE TABLE ip_limits (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           ip VARCHAR(45) NOT NULL,
                           count INT NOT NULL DEFAULT 1,
                           created_at DATETIME NOT NULL,
                           expires_at DATETIME NOT NULL,
                           block_expires_at DATETIME NOT NULL,
                           INDEX idx_ip (ip)
);