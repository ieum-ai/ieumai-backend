-- 테이블 생성 스크립트 (H2용)
DROP TABLE IF EXISTS verification_pins;
DROP TABLE IF EXISTS ip_limits;
DROP TABLE IF EXISTS user_review;
DROP TABLE IF EXISTS test_report;
DROP TABLE IF EXISTS test_script_voice_file;
DROP TABLE IF EXISTS contribution_script_voice_file;
DROP TABLE IF EXISTS contribution;
DROP TABLE IF EXISTS test;
DROP TABLE IF EXISTS test_script;
DROP TABLE IF EXISTS contribution_script;
DROP TABLE IF EXISTS voice_file;
DROP TABLE IF EXISTS contributor;

-- Create tables
CREATE TABLE ip_limits (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           ip VARCHAR(45) NOT NULL,
                           count INT NOT NULL DEFAULT 0,
                           created_at TIMESTAMP NOT NULL,
                           last_request_at TIMESTAMP NOT NULL,
                           block_expires_at TIMESTAMP NULL
);

CREATE TABLE verification_pins (
                                   id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   email VARCHAR(255) NOT NULL,
                                   pin VARCHAR(255) NOT NULL,
                                   attempts INT NOT NULL DEFAULT 0,
                                   created_at TIMESTAMP NOT NULL,
                                   expires_at TIMESTAMP NOT NULL
);

CREATE TABLE contributor (
                             contributor_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             name VARCHAR(255) NOT NULL,
                             email VARCHAR(255) NOT NULL,
                             gender VARCHAR(255) NOT NULL,
                             birthyear INT NOT NULL,
                             state VARCHAR(20) NOT NULL,
                             city VARCHAR(20) NULL
);

CREATE TABLE contribution_script (
                                     contribution_script_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     contribution_script VARCHAR(255) NOT NULL,
                                     contribution_count INT DEFAULT 0,
                                     is_active BOOLEAN DEFAULT TRUE,
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE voice_file (
                            voice_file_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            voice_length BIGINT NOT NULL,
                            path VARCHAR(255) NOT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            source VARCHAR(20) NOT NULL
);

-- 나머지 테이블 생성 스크립트...