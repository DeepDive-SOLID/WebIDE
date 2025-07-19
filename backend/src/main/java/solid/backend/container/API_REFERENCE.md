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
  "containerId": 1,
  "containerName": "My Project",
  "containerContent": "프로젝트 설명",
  "isPublic": true,
  "containerDate": "2024-01-20",
  "ownerName": "홍길동",
  "ownerId": "user123",
  "memberCount": 3,
  "userAuthority": "ROOT"
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
}
```

### 2.2 내 컨테이너 목록
```
GET /api/containers/my
```

### 2.3 공유받은 컨테이너 목록
```
GET /api/containers/shared
```

### 2.4 공개 컨테이너 목록
```
GET /api/containers/public
```

인증 없이도 조회 가능

### 2.5 접근 가능한 모든 컨테이너
```
GET /api/containers
```

소유한 컨테이너 + 참여중인 컨테이너 모두 반환

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
- ROOT 권한 필요
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
```
204 No Content
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
  "invitedMemberIds": ["user1", "user2"],  // 필수, 최대 100명
  "userAuthority": "USER"           // 필수, 현재는 USER만 가능
}
```

#### Response
```json
{
  "memberId": "user1",
  "memberName": "김철수",
  "userAuthority": "USER",
  "joinDate": "2024-01-20T10:00:00",
  "lastActivityDate": "2024-01-20T10:00:00"
}
```

### 5.2 멤버 목록 조회
```
GET /api/containers/{containerId}/members
```

#### Response
```json
[
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
]
```

### 5.3 멤버 제거
```
DELETE /api/containers/{containerId}/members/{targetMemberId}
```

#### Requirements
- ROOT 권한 필요
- 소유자는 제거 불가

### 5.4 컨테이너 탈퇴
```
DELETE /api/containers/{containerId}/members/me
```

#### Requirements
- USER 권한만 가능 (소유자는 탈퇴 불가)

### 5.5 활동 시간 업데이트
```
PUT /api/containers/{containerId}/members/me/activity
```

현재 시간으로 lastActivityDate 업데이트

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
  "ROOT": 5,    // 소유한 컨테이너 수
  "USER": 10    // 참여중인 컨테이너 수
}
```

### 7.2 컨테이너 상세 통계
```
GET /api/containers/{containerId}/statistics
```

#### Response
```json
{
  "memberCount": 10,
  "lastActivityDate": "2024-01-20T15:30:00",
  "activeMemberCount": 7    // 30일 이내 활동
}
```

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
  "updatedCount": 2,
  "requestedCount": 3
}
```

#### Notes
- ROOT 권한이 있는 컨테이너만 변경됨
- 권한이 없는 컨테이너는 무시됨

---

## Error Response Format

모든 에러는 다음 형식으로 반환됩니다:

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

### Common Error Codes
- `CONTAINER_NOT_FOUND` : 컨테이너를 찾을 수 없음
- `UNAUTHORIZED_ACCESS` : 접근 권한 없음
- `DUPLICATE_CONTAINER_NAME` : 중복된 컨테이너 이름
- `INVALID_CONTAINER_NAME` : 유효하지 않은 컨테이너 이름
- `MEMBER_NOT_FOUND` : 멤버를 찾을 수 없음
- `MEMBER_ALREADY_EXISTS` : 이미 존재하는 멤버
- `INVALID_MEMBER` : 유효하지 않은 멤버 ID

---

## 자동 작업

### 비활동 멤버 자동 제거
- **실행 주기**: 매일 새벽 2시 (INACTIVE_MEMBER_CLEANUP_SCHEDULE)
- **비활동 기준**: 6개월 이상 활동 없음 (INACTIVE_MONTHS)
- **제거 대상**: USER 권한 멤버만 (소유자는 제외)
- **구현**: `ContainerServiceImpl.removeInactiveMembers()` 스케줄러

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

## Rate Limiting

현재 구현되지 않음. 향후 추가 예정.
