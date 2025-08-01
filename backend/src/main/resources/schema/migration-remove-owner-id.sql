-- 기존 데이터베이스에서 owner_id 제거를 위한 마이그레이션 스크립트
-- 실행 전 반드시 데이터베이스 백업을 권장합니다

USE webide;

-- 1. 인덱스 제거
DROP INDEX IF EXISTS idx_container_owner ON container;

-- 2. 외래 키 제약조건 확인 및 제거
-- 외래 키 이름은 MySQL 버전에 따라 다를 수 있으므로 먼저 확인
-- SHOW CREATE TABLE container; 로 외래 키 이름 확인 후 실행

-- 일반적인 외래 키 이름 패턴으로 시도
ALTER TABLE container DROP FOREIGN KEY IF EXISTS container_ibfk_2;
ALTER TABLE container DROP FOREIGN KEY IF EXISTS fk_container_owner;

-- 3. owner_id 컬럼 제거
ALTER TABLE container DROP COLUMN IF EXISTS owner_id;

-- 4. 변경사항 확인
DESCRIBE container;