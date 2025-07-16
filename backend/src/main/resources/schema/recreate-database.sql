-- 데이터베이스 재생성 스크립트
-- 주의: 이 스크립트는 모든 데이터를 삭제합니다!

-- 1. 기존 데이터베이스 삭제
DROP DATABASE IF EXISTS webide;

-- 2. 데이터베이스 새로 생성
CREATE DATABASE webide 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 3. 데이터베이스 선택
USE webide;

-- 4. 스키마 생성 (WebIDE.sql 내용 실행)
SOURCE WebIDE.sql;

-- 5. 테스트 데이터 추가 (선택사항)
SOURCE test-data.sql;