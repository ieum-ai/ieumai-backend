-- Create database (if not exists)
CREATE DATABASE IF NOT EXISTS ieumai_db
    CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;

USE ieumai_db;

-- Drop tables if they exist (in correct order due to foreign key constraints)
DROP TABLE IF EXISTS voices;
DROP TABLE IF EXISTS scripts;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS verification_pins;
DROP TABLE IF EXISTS ip_limits;

-- Create ip_limits table
CREATE TABLE ip_limits (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           ip VARCHAR(45) NOT NULL,
                           count INT NOT NULL DEFAULT 0,
                           created_at TIMESTAMP NOT NULL,
                           last_request_at TIMESTAMP NOT NULL,
                           block_expires_at TIMESTAMP NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create verification_pins table
CREATE TABLE verification_pins (
                                   id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   email VARCHAR(255) NOT NULL,
                                   pin VARCHAR(255) NOT NULL,
                                   attempts INT NOT NULL DEFAULT 0,
                                   created_at TIMESTAMP NOT NULL,
                                   expires_at TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create users table
CREATE TABLE users (
                       user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       gender ENUM('Male', 'Female', 'etc'),
                       birthyear INT,
                       state ENUM('강원특별자치도', '경기도', '경상남도', '경상북도', '전라남도', '전라북도',
                           '제주특별자치도', '충청남도', '충청북도', '광주광역시', '대구광역시',
                           '대전광역시', '부산광역시', '서울특별시', '세종특별자치시', '울산광역시',
                           '인천광역시'),
                       city VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create scripts table
CREATE TABLE scripts (
                         script_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         script JSON NOT NULL,
                         script_count BIGINT DEFAULT 0,
                         is_active BOOLEAN NOT NULL DEFAULT TRUE,
                         created_at TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create voices table
CREATE TABLE voices (
                        voice_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        user_id BIGINT,
                        script_id BIGINT,
                        duration DOUBLE NOT NULL,
                        path VARCHAR(255),
                        created_at TIMESTAMP NOT NULL,
                        source ENUM('Contribution', 'Dialect_Test') NOT NULL,
                        voice_length DOUBLE NOT NULL,
                        FOREIGN KEY (user_id) REFERENCES users(user_id),
                        FOREIGN KEY (script_id) REFERENCES scripts(script_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create indexes
CREATE INDEX idx_ip_limits_ip ON ip_limits(ip);
CREATE INDEX idx_verification_pins_email ON verification_pins(email);
CREATE INDEX idx_voices_user_id ON voices(user_id);
CREATE INDEX idx_voices_script_id ON voices(script_id);