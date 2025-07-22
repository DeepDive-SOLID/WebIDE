-- Docker 실행 테이블의 file_path 컬럼을 nullable로 변경
ALTER TABLE docker_execution 
MODIFY COLUMN file_path VARCHAR(255) NULL;

-- 기존 NULL 값들을 처리 (필요한 경우)
UPDATE docker_execution 
SET file_path = NULL 
WHERE file_path = '';