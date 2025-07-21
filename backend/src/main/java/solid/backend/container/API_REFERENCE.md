# Container API Reference

## Base URL
```
/api/containers
```

## Authentication
모든 API는 JWT 토큰을 사용한 인증이 필요합니다. (일부 공개 API 제외)

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
  "timestamp": "2024-01-20T10:00:00",
  "errorCode": "ERROR_CODE",  // 선택적
  "path": "/api/containers/1" // 선택적
}
```

---

## 1. 컨테이너 생성

### Endpoint
```
POST /api/containers
```

### Request Headers
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

### Request Body
```json
{
  "containerName": "string",      // 필수, 1-20자, 특수문자는 하이픈과 언더스코어만 허용
  "containerContent": "string",   // 선택, 최대 200자
  "isPublic": boolean,           // 필수, true=공개, false=비공개
  "invitedMemberIds": ["string"] // 선택, 초대할 멤버 ID 목록
}
```

### Response  
```json
{
  "success": true,
  "data": {
    "containerId": 1,
    "containerName": "My Project",
    "containerContent": "프로젝트 설명",
    "isPublic": true,
    "containerDate": "2024-01-20",
    "ownerName": "홍길동",
    "ownerId": "user123",
    "memberCount": 3,
    "userAuthority": "ROOT"
  },
  "message": "컨테이너가 생성되었습니다",
  "timestamp": "2024-01-20T10:00:00"
}
```

### Error Responses
- `400 Bad Request` : 유효하지 않은 입력값
- `409 Conflict` : 중복된 컨테이너 이름

---

## 2. 컨테이너 조회

### 2.1 특정 컨테이너 조회
```
GET /api/containers/{containerId}
```

#### Path Parameters
- `containerId` : 조회할 컨테이너 ID

#### Response
```json
{
  "success": true,
  "data": {
    "containerId": 1,
    "containerName": "My Project",
    "containerContent": "프로젝트 설명",
    "isPublic": true,
    "containerDate": "2024-01-20",
    "ownerName": "홍길동",
    "ownerId": "user123",
    "memberCount": 3,
    "userAuthority": "ROOT",
    "members": [...]  // 멤버인 경우에만 포함
  },
  "timestamp": "2024-01-20T10:00:00"
}
```

### 2.2 내 컨테이너 목록
```
GET /api/containers/my
```

#### Response
```json
{
  "success": true,
  "data": [
    {
      "containerId": 1,
      "containerName": "My Project",
      "containerContent": "내 프로젝트",
      "isPublic": false,
      "containerDate": "2024-01-20",
      "ownerName": "홍길동",
      "ownerId": "user123",
      "memberCount": 3,
      "userAuthority": "ROOT"
    }
  ],
  "timestamp": "2024-01-20T10:00:00"
}
```

### 2.3 공유받은 컨테이너 목록
```
GET /api/containers/shared
```

#### Response
```json
{
  "success": true,
  "data": [
    {
      "containerId": 2,
      "containerName": "Team Project",
      "containerContent": "팀 프로젝트",
      "isPublic": false,
      "containerDate": "2024-01-15",
      "ownerName": "김철수",
      "ownerId": "user456",
      "memberCount": 5,
      "userAuthority": "USER"
    }
  ],
  "timestamp": "2024-01-20T10:00:00"
}
```

### 2.4 공개 컨테이너 목록
```
GET /api/containers/public
```

인증 없이도 조회 가능

#### Response
```json
{
  "success": true,
  "data": [
    {
      "containerId": 3,
      "containerName": "Public Demo",
      "containerContent": "공개 프로젝트",
      "isPublic": true,
      "containerDate": "2024-01-10",
      "ownerName": "이영희",
      "ownerId": "user789",
      "memberCount": 15,
      "userAuthority": null  // 비인증 사용자인 경우
    }
  ],
  "timestamp": "2024-01-20T10:00:00"
}
```

### 2.5 접근 가능한 모든 컨테이너
```
GET /api/containers
```

소유한 컨테이너 + 참여중인 컨테이너 모두 반환

#### Response
```json
{
  "success": true,
  "data": [
    // 소유한 컨테이너와 참여중인 컨테이너가 모두 포함됨
  ],
  "timestamp": "2024-01-20T10:00:00"
}
```

---

## 3. 컨테이너 수정

### Endpoint
```
PUT /api/containers/{containerId}
```

### Request Body
```json
{
  "containerName": "string",      // 선택, 1-20자
  "containerContent": "string",   // 선택, 최대 200자
  "isPublic": boolean            // 선택
}
```

### Requirements
- 기본적으로 모든 멤버 수정 가능
- isPublic 변경은 ROOT 권한 필요
- 최소 하나 이상의 필드 필요

---

## 4. 컨테이너 삭제

### Endpoint
```
DELETE /api/containers/{containerId}
```

### Requirements
- ROOT 권한 필요
- 관련된 모든 데이터가 함께 삭제됨

### Response
```json
{
  "success": true,
  "data": null,
  "message": "컨테이너가 삭제되었습니다",
  "timestamp": "2024-01-20T10:00:00"
}
```

---

## 5. 멤버 관리

### 5.1 멤버 초대
```
POST /api/containers/{containerId}/members
```

#### Request Body
```json
{
  "memberId": "user1"    // 필수, 초대할 사용자 ID
}
```

#### Response
```json
{
  "success": true,
  "data": {
    "memberId": "user1",
    "memberName": "김철수",
    "userAuthority": "USER",
    "joinDate": "2024-01-20T10:00:00",
    "lastActivityDate": "2024-01-20T10:00:00"
  },
  "message": "멤버가 초대되었습니다",
  "timestamp": "2024-01-20T10:00:00"
}
```

### 5.2 멤버 목록 조회
```
GET /api/containers/{containerId}/members
```

#### Response
```json
{
  "success": true,
  "data": [
    {
      "memberId": "user1",
      "memberName": "김철수",
      "userAuthority": "ROOT",
      "joinDate": "2024-01-20T10:00:00",
      "lastActivityDate": "2024-01-20T10:00:00"
    },
    {
      "memberId": "user2",
      "memberName": "이영희",
      "userAuthority": "USER",
      "joinDate": "2024-01-20T11:00:00",
      "lastActivityDate": "2024-01-20T15:00:00"
    }
  ],
  "timestamp": "2024-01-20T10:00:00"
}
```

### 5.3 멤버 제거
```
DELETE /api/containers/{containerId}/members/{targetMemberId}
```

#### Requirements
- ROOT 권한 필요
- 소유자는 제거 불가

#### Response
```json
{
  "success": true,
  "data": null,
  "message": "멤버가 제거되었습니다",
  "timestamp": "2024-01-20T10:00:00"
}
```

### 5.4 컨테이너 탈퇴
```
DELETE /api/containers/{containerId}/members/me
```

#### Requirements
- USER 권한만 가능 (소유자는 탈퇴 불가)

#### Response
```json
{
  "success": true,
  "data": null,
  "message": "컨테이너에서 탈퇴했습니다",
  "timestamp": "2024-01-20T10:00:00"
}
```

### 5.5 활동 시간 업데이트
```
PUT /api/containers/{containerId}/members/me/activity
```

현재 시간으로 lastActivityDate 업데이트

#### Response
```json
{
  "success": true,
  "data": null,
  "message": "활동 시간이 업데이트되었습니다",
  "timestamp": "2024-01-20T10:00:00"
}
```

---

## 6. 검색

### 6.1 간단 검색
```
GET /api/containers/search
```

#### Query Parameters
- `name` : 컨테이너 이름 (부분 일치)
- `isPublic` : 공개 여부
- `ownerId` : 소유자 ID

#### Example
```
GET /api/containers/search?name=project&isPublic=true
```

### 6.2 고급 검색
```
POST /api/containers/search
```

#### Request Body
```json
{
  "name": "string",
  "isPublic": boolean,
  "ownerId": "string",
  "memberId": "string"
}
```

#### Response
```json
{
  "success": true,
  "data": [
    {
      "containerId": 1,
      "containerName": "Project 1",
      "containerContent": "설명",
      "isPublic": true,
      "containerDate": "2024-01-20",
      "ownerName": "...",
      "ownerId": "...",
      "memberCount": 5,
      "userAuthority": "USER"
    }
  ],
  "message": "검색이 완료되었습니다",
  "timestamp": "2024-01-20T10:00:00"
}
```

---

## 7. 통계

### 7.1 권한별 컨테이너 통계
```
GET /api/containers/stats/authority
```

#### Response
```json
{
  "success": true,
  "data": {
    "ROOT": 5,    // 소유한 컨테이너 수
    "USER": 10    // 참여중인 컨테이너 수
  },
  "timestamp": "2024-01-20T10:00:00"
}
```

### 7.2 컨테이너 상세 통계
```
GET /api/containers/{containerId}/statistics
```

#### Response
```json
{
  "success": true,
  "data": {
    "containerId": 1,
    "containerName": "My Project",
    "totalMemberCount": 10,
    "activeMemberCount": 0,        // 현재 접속 중 (웹소켓 미구현으로 0)
    "inactiveMemberCount": 10,     // 현재 미접속 (웹소켓 미구현으로 전체)
    "lastActivityDate": "2024-01-20T15:30:00",
    "createdDate": "2024-01-15T00:00:00",
    "rootMemberCount": 1,          // ROOT 권한 사용자 수
    "userMemberCount": 9,          // USER 권한 사용자 수
    "activityRate": 0.0            // 활동률 (현재 0%)
  },
  "timestamp": "2024-01-20T10:00:00"
}
```

#### Notes
- 실시간 접속 상태는 WebSocket 구현 후 업데이트 예정

---

## 8. 일괄 작업

### 8.1 공개 상태 일괄 변경
```
PUT /api/containers/batch/visibility
```

#### Request Body
```json
{
  "containerIds": [1, 2, 3],    // 필수, 최대 100개
  "isPublic": false             // 필수
}
```

#### Response
```json
{
  "success": true,
  "data": {
    "updatedCount": 2,
    "requestedCount": 3
  },
  "message": "2개의 컨테이너가 업데이트되었습니다",
  "timestamp": "2024-01-20T10:00:00"
}
```

#### Notes
- ROOT 권한이 있는 컨테이너만 변경됨
- 권한이 없는 컨테이너는 무시됨
- updatedCount는 실제 변경된 컨테이너 수

---

## Error Response Format

모든 에러는 ApiResponse 형식으로 반환됩니다:

```json
{
  "success": false,
  "data": null,
  "message": "에러 메시지",
  "timestamp": "2024-01-20T10:00:00",
  "errorCode": "ERROR_CODE",
  "path": "/api/containers/..."
}
```

### Error Codes
- `CONTAINER_NOT_FOUND` : 컨테이너를 찾을 수 없음
- `UNAUTHORIZED_ACCESS` : 권한 없음
- `DUPLICATE_MEMBER` : 이미 존재하는 멤버
- `MEMBER_NOT_FOUND` : 멤버를 찾을 수 없음
- `INVALID_MEMBER` : 유효하지 않은 멤버
- `VALIDATION_FAILED` : 입력값 검증 실패
- `INVALID_ARGUMENT` : 잘못된 인자
- `MISSING_HEADER` : 필수 헤더 누락
- `INTERNAL_SERVER_ERROR` : 서버 오류

---

## 버전 히스토리

### 2025-07-21 (v1.2.0)
- 모든 API 응답을 ApiResponse 형식으로 통일
- 성공/실패 응답 형식 표준화
- 응답에 timestamp 필드 추가
- 메시지 필드를 통한 상세 안내 제공

### 2025-07-21 (v1.1.0)
- 멤버 초대 API 형식 단순화 (invitedMemberIds 대신 memberId 사용)
- 컨테이너 통계 API 응답 형식 상세화
- 실시간 접속 상태 필드 추가 (WebSocket 미구현으로 현재 0)
- Error Response 형식 및 Error Code 목록 추가
- 레포지토리 구조 개선 (QueryDSL 커스텀 패턴)

---

## 자동 작업

### 비활동 멤버 자동 제거
- **실행 주기**: 매일 새벽 2시
- **비활동 기준**: 6개월 이상 활동 없음
- **제거 대상**: USER 권한 멤버만 (소유자는 제외)
- **구현**: `@Scheduled(cron = "0 0 2 * * *")`

---

## 상수 정의 (ContainerConstants)

### 권한 상수
- `AUTHORITY_ROOT`: "ROOT" - 관리자 권한
- `AUTHORITY_USER`: "USER" - 일반 사용자 권한

### 제한 사항 상수
- `CONTAINER_NAME_MIN_LENGTH`: 1 - 컨테이너 이름 최소 길이
- `CONTAINER_NAME_MAX_LENGTH`: 20 - 컨테이너 이름 최대 길이
- `CONTAINER_CONTENT_MAX_LENGTH`: 200 - 컨테이너 설명 최대 길이
- `BATCH_OPERATION_MAX_SIZE`: 100 - 일괄 작업 최대 개수
- `INACTIVE_MONTHS`: 6 - 비활동 기준 개월수

### 컨테이너 이름 규칙
- 1-20자 길이
- 허용 문자: 알파벳(a-z, A-Z), 숫자(0-9), 한글, 공백, 하이픈(-), 언더스코어(_)
- 정규식: `^[a-zA-Z0-9가-힣\\s\\-_]+$`

---

## API 호출 제한

현재 구현되지 않음. 향후 추가 예정.

---

## 참고 사항

### 테스트 환경
- 개발 환경에서는 JwtFilter가 memberId 헤더를 허용하도록 설정됨
- 프로덕션에서는 반드시 JWT 토큰 사용

### 데이터베이스
- 기본 포트: 8081
- MySQL 사용 (local 프로필)
