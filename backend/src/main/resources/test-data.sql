-- 테스트 데이터 삽입 스크립트

-- 1. Member 데이터
INSERT INTO member (member_id, member_pw, member_name, member_mail, member_phone, member_birth, member_img) 
VALUES ('testuser1', '$2a$10$YourHashedPasswordHere', '테스트유저', 'test@example.com', '010-1234-5678', '1990-01-01', null)
ON DUPLICATE KEY UPDATE member_name = member_name;

-- 2. Team 데이터
INSERT INTO team (team_id, team_name, team_create_date) 
VALUES (1, '테스트 팀', CURRENT_DATE)
ON DUPLICATE KEY UPDATE team_name = team_name;

-- 3. Container 데이터
INSERT INTO container (container_id, team_id, is_public, container_date, container_nm, container_content, owner_id)
VALUES (1, 1, true, CURRENT_DATE, '테스트 컨테이너', '테스트용 컨테이너입니다', 'testuser1')
ON DUPLICATE KEY UPDATE container_nm = container_nm;

-- 4. TeamUser 데이터 (testuser1을 팀 멤버로 추가)
INSERT INTO team_user (team_user_id, team_id, team_auth_id, member_id, joined_date)
VALUES (1, 1, 1, 'testuser1', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE joined_date = joined_date;

-- 5. Auth 데이터 (없다면)
INSERT INTO auth (team_auth_id, team_auth_name) 
VALUES (1, 'MEMBER'), (2, 'ROOT')
ON DUPLICATE KEY UPDATE team_auth_name = team_auth_name;