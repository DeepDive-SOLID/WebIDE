# Docker 설정 가이드

## 1. Docker Desktop 설정 (Windows)

### TCP 포트 활성화
1. Docker Desktop 설정 열기
2. "General" 탭에서 "Expose daemon on tcp://localhost:2375 without TLS" 체크
3. Docker Desktop 재시작

### WSL2에서 Docker 사용
```bash
# Docker 상태 확인
docker version

# Docker 데몬 연결 테스트
curl http://localhost:2375/version
```

## 2. 애플리케이션 실행

### Docker 프로파일과 함께 실행
```bash
./gradlew bootRun --args='--spring.profiles.active=docker'
```

### 또는 환경 변수로 설정
```bash
export SPRING_PROFILES_ACTIVE=docker
./gradlew bootRun
```

## 3. 문제 해결

### Docker 연결 실패 시
1. Docker Desktop이 실행 중인지 확인
2. TCP 포트가 활성화되어 있는지 확인
3. 방화벽이 2375 포트를 차단하지 않는지 확인

### 데이터베이스 마이그레이션
```sql
-- file_path를 nullable로 변경
ALTER TABLE docker_execution 
MODIFY COLUMN file_path VARCHAR(255) NULL;
```

## 4. 테스트
```bash
# Docker API 테스트
./test-docker.sh
```