USE webide;

-- Additional test members
INSERT INTO `member` (`MEMBER_ID`, `MEMBER_NAME`, `MEMBER_PW`, `MEMBER_EMAIL`) VALUES
('test004', 'TestUser4', 'password123', 'test4@example.com'),
('test005', 'TestUser5', 'password123', 'test5@example.com'),
('admin001', 'AdminUser', 'admin123', 'admin@example.com');

-- Sample container with team (ID will be 1)
INSERT INTO team (team_name) VALUES ('Sample Project Team');
SET @team_id = LAST_INSERT_ID();

INSERT INTO container (team_id, container_auth, container_date, container_nm, container_content) 
VALUES (@team_id, false, CURDATE(), 'Sample Project', 'This is a sample private project');

-- Add members to the sample container
INSERT INTO team_user (team_id, team_auth_id, member_id, joined_date, last_activity_date) VALUES
(@team_id, 'ROOT', 'test001', NOW(), NOW()),
(@team_id, 'WRITE', 'test002', NOW(), NOW()),
(@team_id, 'USER', 'test003', NOW(), NOW());