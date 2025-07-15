-- 데이터베이스 생성 스크립트
-- MySQL 콘솔이나 워크벤치에서 실행

-- 1. 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS webide 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 2. 데이터베이스 사용
USE webide;

-- 3. 사용자에게 권한 부여 (필요한 경우)
-- GRANT ALL PRIVILEGES ON webide.* TO 'your_username'@'localhost';
-- FLUSH PRIVILEGES;