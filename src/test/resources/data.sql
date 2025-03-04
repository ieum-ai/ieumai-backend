-- 테스트용 스크립트 데이터
INSERT INTO contribution_script (contribution_script_id, contribution_script, contribution_count, is_active, created_at)
VALUES (1, '테스트용 기여 스크립트 1입니다. 이 문장은 방언 분류 모델을 위한 테스트에 사용됩니다.', 0, true, CURRENT_TIMESTAMP);

INSERT INTO contribution_script (contribution_script_id, contribution_script, contribution_count, is_active, created_at)
VALUES (2, '테스트용 기여 스크립트 2입니다. 다양한 억양과 방언을 확인하기 위한 문장입니다.', 0, true, CURRENT_TIMESTAMP);

-- 테스트용 사용자 데이터
INSERT INTO contributor (contributor_id, name, email, gender, birthyear, state, city)
VALUES (1, '테스트사용자', 'test@example.com', '남성', 1990, '서울특별시', '서울시');