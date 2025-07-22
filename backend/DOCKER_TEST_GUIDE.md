# Docker 실행 기능 테스트 가이드

## 1. 사전 준비

### Docker Desktop 설치 및 WSL 통합
1. Docker Desktop 설치
2. Settings → Resources → WSL Integration에서 WSL 활성화
3. WSL 터미널에서 `docker --version` 확인

### MySQL 실행
```bash
docker run -d --name mysql-webide \
  -e MYSQL_ROOT_PASSWORD=gg940701 \
  -e MYSQL_DATABASE=webide \
  -p 3306:3306 \
  mysql:8.0
```

### Spring Boot 애플리케이션 실행
```bash
cd backend
./gradlew bootRun -Dspring.profiles.active=local
```

## 2. 테스트 준비

### 2.1 JWT 토큰 획득
```bash
# 테스트용 토큰 생성
curl http://localhost:8081/test/token/testuser1

# 응답 예시
{
  "status": "SUCCESS",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "memberId": "testuser1"
  },
  "message": "테스트 토큰 생성 성공"
}
```

### 2.2 테스트 데이터 준비
DB에 다음 데이터가 필요합니다:
- Member (memberId: testuser1)
- Team 
- Container
- TeamUser (testuser1이 팀 멤버)

## 3. Docker 실행 기능 테스트

### 3.1 코드 실행 테스트

#### Python 코드 실행
```bash
curl -X POST http://localhost:8081/api/docker/execute \
  -H "Authorization: Bearer {accessToken}" \
  -H "Content-Type: application/json" \
  -d '{
    "containerId": 1,
    "language": "python",
    "code": "print(\"Hello, World!\")\nfor i in range(5):\n    print(f\"Number: {i}\")",
    "input": ""
  }'
```

#### Java 코드 실행
```bash
curl -X POST http://localhost:8081/api/docker/execute \
  -H "Authorization: Bearer {accessToken}" \
  -H "Content-Type: application/json" \
  -d '{
    "containerId": 1,
    "language": "java",
    "code": "public class Main {\n    public static void main(String[] args) {\n        System.out.println(\"Hello from Java!\");\n    }\n}",
    "input": ""
  }'
```

#### JavaScript 코드 실행
```bash
curl -X POST http://localhost:8081/api/docker/execute \
  -H "Authorization: Bearer {accessToken}" \
  -H "Content-Type: application/json" \
  -d '{
    "containerId": 1,
    "language": "javascript",
    "code": "console.log(\"Hello from JavaScript!\");\nconst arr = [1, 2, 3, 4, 5];\narr.forEach(n => console.log(`Number: ${n}`));",
    "input": ""
  }'
```

#### C++ 코드 실행
```bash
curl -X POST http://localhost:8081/api/docker/execute \
  -H "Authorization: Bearer {accessToken}" \
  -H "Content-Type: application/json" \
  -d '{
    "containerId": 1,
    "language": "cpp",
    "code": "#include <iostream>\nusing namespace std;\n\nint main() {\n    cout << \"Hello from C++!\" << endl;\n    return 0;\n}",
    "input": ""
  }'
```

### 3.2 실행 상태 조회
```bash
curl http://localhost:8081/api/docker/executions/{executionId}/status \
  -H "Authorization: Bearer {accessToken}"
```

### 3.3 실행 중지
```bash
curl -X POST http://localhost:8081/api/docker/executions/{executionId}/stop \
  -H "Authorization: Bearer {accessToken}"
```

### 3.4 실행 이력 조회
```bash
curl http://localhost:8081/api/docker/containers/1/executions \
  -H "Authorization: Bearer {accessToken}"
```

### 3.5 통계 조회
```bash
# 컨테이너 통계
curl http://localhost:8081/api/docker/containers/1/statistics \
  -H "Authorization: Bearer {accessToken}"

# 언어별 통계
curl http://localhost:8081/api/docker/containers/1/statistics/languages \
  -H "Authorization: Bearer {accessToken}"
```

## 4. 파일 동기화 테스트

### 4.1 파일 트리 조회
```bash
curl http://localhost:8081/api/docker/containers/1/files \
  -H "Authorization: Bearer {accessToken}"
```

### 4.2 파일 동기화
```bash
curl -X POST http://localhost:8081/api/docker/containers/sync \
  -H "Authorization: Bearer {accessToken}" \
  -H "Content-Type: application/json" \
  -d '{
    "containerId": 1,
    "syncMode": "FULL"
  }'
```

## 5. 에러 케이스 테스트

### 5.1 무한 루프 (타임아웃)
```bash
curl -X POST http://localhost:8081/api/docker/execute \
  -H "Authorization: Bearer {accessToken}" \
  -H "Content-Type: application/json" \
  -d '{
    "containerId": 1,
    "language": "python",
    "code": "while True:\n    pass",
    "input": ""
  }'
```

### 5.2 메모리 초과
```bash
curl -X POST http://localhost:8081/api/docker/execute \
  -H "Authorization: Bearer {accessToken}" \
  -H "Content-Type: application/json" \
  -d '{
    "containerId": 1,
    "language": "python",
    "code": "data = []\nwhile True:\n    data.append(\"x\" * 1000000)",
    "input": ""
  }'
```

### 5.3 위험한 명령어
```bash
curl -X POST http://localhost:8081/api/docker/execute \
  -H "Authorization: Bearer {accessToken}" \
  -H "Content-Type: application/json" \
  -d '{
    "containerId": 1,
    "language": "python",
    "code": "import os\nos.system(\"rm -rf /\")",
    "input": ""
  }'
```

## 6. Postman Collection

Postman에서 더 쉽게 테스트하려면:

1. 새 Collection 생성
2. Variables에 추가:
   - `baseUrl`: http://localhost:8081
   - `accessToken`: (토큰 값)

3. 각 API별 Request 생성
4. Authorization 탭에서 Bearer Token 선택, {{accessToken}} 입력

## 7. 주의사항

1. Docker Desktop이 실행 중이어야 함
2. MySQL이 실행 중이어야 함
3. 테스트 데이터(Member, Team, Container)가 DB에 있어야 함
4. 첫 실행 시 Docker 이미지 다운로드로 시간이 걸릴 수 있음