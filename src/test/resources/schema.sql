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

CREATE TABLE contribution (
                              contribution_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              contributor_id BIGINT NOT NULL,
                              FOREIGN KEY (contributor_id) REFERENCES contributor(contributor_id)
);


CREATE TABLE contribution_script_voice_file (
                                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                contribution_id BIGINT NOT NULL,
                                                contribution_script_id BIGINT NOT NULL,
                                                voice_file_id BIGINT NOT NULL,
                                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                FOREIGN KEY (contribution_id) REFERENCES contribution(contribution_id),
                                                FOREIGN KEY (contribution_script_id) REFERENCES contribution_script(contribution_script_id),
                                                FOREIGN KEY (voice_file_id) REFERENCES voice_file(voice_file_id)
);

CREATE TABLE test (
                      test_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      UUID VARCHAR(36) UNIQUE NOT NULL
);

CREATE TABLE test_script (
                             test_script_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             test_script VARCHAR(255) NOT NULL,
                             test_count INT DEFAULT 0,
                             is_active BOOLEAN DEFAULT TRUE,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE test_report (
                             test_id BIGINT NOT NULL,
                             test_report JSON,
                             report_version INT NOT NULL,
                             PRIMARY KEY (test_id, report_version),
                             FOREIGN KEY (test_id) REFERENCES test(test_id)
);

CREATE TABLE test_script_voice_file (
                                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                        test_id BIGINT NOT NULL,
                                        test_script_id BIGINT NOT NULL,
                                        voice_file_id BIGINT,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        FOREIGN KEY (test_id) REFERENCES test(test_id),
                                        FOREIGN KEY (test_script_id) REFERENCES test_script(test_script_id),
                                        FOREIGN KEY (voice_file_id) REFERENCES voice_file(voice_file_id)
);

CREATE TABLE user_review (
                             user_review_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             test_voice_id BIGINT NOT NULL,
                             state VARCHAR(20) NOT NULL,
                             city VARCHAR(20),
                             FOREIGN KEY (test_voice_id) REFERENCES test_script_voice_file(id)
);

-- Create indexes
CREATE INDEX idx_ip_limits_ip ON ip_limits(ip);
CREATE INDEX idx_verification_pins_email ON verification_pins(email);
CREATE INDEX idx_contributor_email ON contributor(email);
CREATE INDEX idx_voice_file_path ON voice_file(path);
CREATE INDEX idx_test_uuid ON test(UUID);

-- H2 doesn't support direct ENUM type, so we'll use CHECK constraints instead

-- State Check Constraint for contributor
ALTER TABLE contributor ADD CONSTRAINT chk_contributor_state CHECK (
    state IN (
              '강원특별자치도', '경기도', '경상남도', '경상북도', '전라남도',
              '전라북도', '제주특별자치도', '충청남도', '충청북도', '광주광역시',
              '대구광역시', '대전광역시', '부산광역시', '서울특별시',
              '세종특별자치시', '울산광역시', '인천광역시'
        )
    );

-- State Check Constraint for user_review
ALTER TABLE user_review ADD CONSTRAINT chk_user_review_state CHECK (
    state IN (
              '강원특별자치도', '경기도', '경상남도', '경상북도', '전라남도',
              '전라북도', '제주특별자치도', '충청남도', '충청북도', '광주광역시',
              '대구광역시', '대전광역시', '부산광역시', '서울특별시',
              '세종특별자치시', '울산광역시', '인천광역시'
        )
    );

-- City Check Constraint for contributor
ALTER TABLE contributor ADD CONSTRAINT chk_contributor_city CHECK (
    city IN (
        -- 경기도
             '고양시', '과천시', '광명시', '광주시', '구리시', '군포시', '김포시',
             '남양주시', '동두천시', '부천시', '성남시', '수원시', '시흥시', '안산시',
             '안성시', '안양시', '양주시', '여주시', '오산시', '용인시', '의왕시',
             '의정부시', '이천시', '파주시', '평택시', '포천시', '하남시', '화성시',

        -- 강원특별자치도
             '강릉시', '동해시', '삼척시', '속초시', '원주시', '춘천시', '태백시',

        -- 경상북도
             '경산시', '경주시', '구미시', '김천시', '문경시', '상주시', '안동시',
             '영주시', '영천시', '포항시',

        -- 제주특별자치도
             '서귀포시', '제주시',

        -- 전라남도
             '광양시', '나주시', '목포시', '순천시', '여수시',

        -- 전라북도
             '군산시', '김제시', '남원시', '익산시', '전주시', '정읍시',

        -- 충청남도
             '계룡시', '공주시', '논산시', '당진시', '보령시', '서산시', '아산시', '천안시',

        -- 충청북도
             '제천시', '청주시', '충주시'
        )
    );

-- City Check Constraint for user_review
ALTER TABLE user_review ADD CONSTRAINT chk_user_review_city CHECK (
    city IN (
        -- 경기도
             '고양시', '과천시', '광명시', '광주시', '구리시', '군포시', '김포시',
             '남양주시', '동두천시', '부천시', '성남시', '수원시', '시흥시', '안산시',
             '안성시', '안양시', '양주시', '여주시', '오산시', '용인시', '의왕시',
             '의정부시', '이천시', '파주시', '평택시', '포천시', '하남시', '화성시',

        -- 강원특별자치도
             '강릉시', '동해시', '삼척시', '속초시', '원주시', '춘천시', '태백시',

        -- 경상북도
             '경산시', '경주시', '구미시', '김천시', '문경시', '상주시', '안동시',
             '영주시', '영천시', '포항시',

        -- 제주특별자치도
             '서귀포시', '제주시',

        -- 전라남도
             '광양시', '나주시', '목포시', '순천시', '여수시',

        -- 전라북도
             '군산시', '김제시', '남원시', '익산시', '전주시', '정읍시',

        -- 충청남도
             '계룡시', '공주시', '논산시', '당진시', '보령시', '서산시', '아산시', '천안시',

        -- 충청북도
             '제천시', '청주시', '충주시'
        )
    );