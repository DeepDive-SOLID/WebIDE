-- Docker 실행 테이블의 file_path 컬럼을 nullable로 변경
-- 이 파일은 수동으로 실행하거나 spring.sql.init.mode=always로 자동 실행됩니다

-- 컬럼이 이미 nullable인지 확인하고 아닌 경우에만 변경
ALTER TABLE docker_execution 
MODIFY COLUMN file_path VARCHAR(255) NULL;