# Docker Execution API Reference

## Base URL
```
/api/docker
```

## Authentication
모든 Docker API는 JWT 토큰을 사용한 인증이 필요합니다.

```
Authorization: Bearer {access_token}
```

## Response Format
모든 API 응답은 통일된 ApiResponse 형식을 사용합니다:

### 성공 응답
```json
{
  "success": true,
  "data": { ... },           // 응답 데이터
  "message": "성공 메시지",   // 선택적
  "timestamp": "2024-01-20T10:00:00"
}
```

### 오류 응답
```json
{
  "success": false,
  "data": null,
  "message": "오류 메시지",
  "timestamp": "2024-01-20T10:00:00"
}
```

---

## 1. 코드 실행

### Endpoint
```
POST /api/docker/execute
```

### Request Headers
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

### Request Body
```json
{
  "language": "string",      // 필수, python|java|javascript|cpp|c
  "code": "string",         // 필수, 실행할 코드
  "input": "string",        // 선택, 표준 입력
  "containerId": 1,         // 필수, 컨테이너 ID
  "filePath": "string"      // 선택, 파일 경로
}
```

### Response
```json
{
  "success": true,
  "data": {
    "executionId": 1,
    "language": "python",
    "code": "print('Hello, World!')",
    "input": null,
    "output": "Hello, World!\n",
    "errorOutput": "",
    "status": "COMPLETED",
    "executionTime": 150,
    "memoryUsed": 52428800,
    "createdAt": "2024-01-20T10:30:00",
    "completedAt": "2024-01-20T10:30:01"
  },
  "message": "코드 실행이 완료되었습니다",
  "timestamp": "2024-01-20T10:30:01"
}
```

### Error Cases
- 400: 잘못된 요청 (언어 미지원, 코드 누락 등)
- 403: 컨테이너 접근 권한 없음
- 500: 코드 실행 실패

---

## 2. 실행 상태 조회

### Endpoint
```
GET /api/docker/executions/{executionId}/status
```

### Response
```json
{
  "success": true,
  "data": {
    "executionId": 1,
    "status": "RUNNING",
    "message": "실행 중",
    "progress": 50
  },
  "message": "실행 상태 조회 성공",
  "timestamp": "2024-01-20T10:30:00"
}
```

### Status Values
- PENDING: 실행 대기 중
- RUNNING: 실행 중
- COMPLETED: 실행 완료
- ERROR: 실행 오류
- TIMEOUT: 시간 초과

---

## 3. 실행 중지

### Endpoint
```
POST /api/docker/executions/{executionId}/stop
```

### Response
```json
{
  "success": true,
  "data": null,
  "message": "실행이 중지되었습니다",
  "timestamp": "2024-01-20T10:30:00"
}
```

---

## 4. 실행 기록 조회

### Endpoint
```
GET /api/docker/containers/{containerId}/executions
```

### Response
```json
{
  "success": true,
  "data": [
    {
      "executionId": 1,
      "language": "python",
      "code": "print('Hello')",
      "output": "Hello\n",
      "status": "COMPLETED",
      "executionTime": 150,
      "createdAt": "2024-01-20T10:30:00"
    }
  ],
  "message": "실행 기록 조회 성공",
  "timestamp": "2024-01-20T10:30:00"
}
```

---

## 5. 실행 기록 삭제

### Endpoint
```
DELETE /api/docker/executions/{executionId}
```

### Response
```json
{
  "success": true,
  "data": null,
  "message": "실행 기록이 삭제되었습니다",
  "timestamp": "2024-01-20T10:30:00"
}
```

---

## 6. 지원 언어 목록

### Endpoint
```
GET /api/docker/languages
```

### Response
```json
{
  "success": true,
  "data": ["python", "java", "javascript", "cpp", "c"],
  "message": "지원 언어 목록 조회 성공",
  "timestamp": "2024-01-20T10:30:00"
}
```

---

## 코드 실행 제한사항

### 리소스 제한
- 메모리: 512MB
- CPU: 0.5 Core
- 실행 시간: 30초

### 보안 제한
- 파일 시스템 접근 제한
- 네트워크 접근 차단
- 위험한 시스템 명령어 차단
- 코드 길이: 최대 10,000자

### 금지된 기능
- Python: eval(), exec(), __import__(), os, subprocess, socket
- Java: Runtime.exec(), ProcessBuilder, File I/O
- JavaScript: eval(), require('child_process'), require('fs')
- C/C++: system(), exec 계열 함수

---

## 통계 API

### 실행 통계 조회
```
GET /api/docker/containers/{containerId}/statistics?startDate=2024-01-01&endDate=2024-12-31
```

### Response
```json
{
  "success": true,
  "data": {
    "totalCount": 150,
    "avgExecutionTime": 250.5,
    "avgMemoryUsed": 52428800.0
  },
  "message": "통계 조회 성공",
  "timestamp": "2024-01-20T10:30:00"
}
```

### 언어별 통계 조회
```
GET /api/docker/containers/{containerId}/statistics/languages
```

### Response
```json
{
  "success": true,
  "data": [
    {
      "language": "python",
      "count": 80,
      "avgExecutionTime": 200.0
    },
    {
      "language": "java",
      "count": 50,
      "avgExecutionTime": 300.0
    }
  ],
  "message": "언어별 통계 조회 성공",
  "timestamp": "2024-01-20T10:30:00"
}
```

---

## 에러 응답 형식

```json
{
  "success": false,
  "data": null,
  "message": "에러 메시지",
  "timestamp": "2024-01-20T10:30:00"
}
```

### 공통 에러 코드
- 400: Bad Request - 잘못된 요청
- 401: Unauthorized - 인증 필요
- 403: Forbidden - 권한 없음
- 404: Not Found - 리소스를 찾을 수 없음
- 500: Internal Server Error - 서버 오류