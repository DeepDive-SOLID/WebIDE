USE webide;

-- Additional test members
INSERT INTO `member` (`member_id`, `member_name`, `member_pw`, `member_email`) VALUES
('test004', 'TestUser4', 'password123', 'test4@example.com'),
('test005', 'TestUser5', 'password123', 'test5@example.com'),
('admin001', 'AdminUser', 'admin123', 'admin@example.com');

-- Sample container with group
INSERT INTO `group_table` (`group_name`) VALUES ('Sample Project Group');
SET @group_id = LAST_INSERT_ID();

INSERT INTO `container` (`group_id`, `container_auth`, `container_date`, `container_nm`, `container_content`, `owner_id`) 
VALUES (@group_id, false, CURDATE(), 'Sample Project', 'This is a sample private project', 'test001');

-- Add members to the sample container
INSERT INTO `group_member` (`group_id`, `group_auth_id`, `member_id`) VALUES
(@group_id, 'ROOT', 'test001'),
(@group_id, 'USER', 'test002'),
(@group_id, 'USER', 'test003');