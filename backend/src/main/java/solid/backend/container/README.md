# Container Module

## 개요
컨테이너는 WebIDE의 핵심 작업 공간으로, 사용자가 코드를 작성하고 프로젝트를 관리하는 독립적인 환경입니다.

## 주요 구성 요소

### Controller
- **ContainerController.java** : 컨테이너 관련 REST API 엔드포인트 제공

### Service
- **ContainerService.java** : 컨테이너 비즈니스 로직 인터페이스
- **ContainerServiceImpl.java** : 컨테이너 비즈니스 로직 구현체

### DTO (Data Transfer Objects)
- **ContainerCreateDto.java** : 컨테이너 생성 요청 데이터
- **ContainerUpdateDto.java** : 컨테이너 정보 수정 요청 데이터
- **ContainerResponseDto.java** : 컨테이너 응답 데이터
- **ContainerSearchDto.java** : 컨테이너 검색 조건 데이터
- **ContainerStatisticsDto.java** : 컨테이너 통계 정보
- **MemberInviteDto.java** : 멤버 초대 요청 데이터
- **GroupMemberResponseDto.java** : 그룹 멤버 정보 응답 데이터
- **BatchContainerVisibilityDto.java** : 일괄 공개 상태 변경 요청 데이터

### Exception
- **ContainerExceptionHandler.java** : 컨테이너 관련 예외 처리
- **ContainerNotFoundException.java** : 컨테이너를 찾을 수 없을 때
- **DuplicateMemberException.java** : 중복된 멤버 추가 시도
- **InvalidMemberException.java** : 유효하지 않은 멤버
- **MemberNotFoundException.java** : 멤버를 찾을 수 없을 때
- **UnauthorizedContainerAccessException.java** : 권한 없는 접근

### Constants
- **ContainerConstants.java** : 컨테이너 모듈의 상수 정의 (권한, 유효성 검사, 에러 메시지 등)

### Common
- **ApiResponse.java** : 통합 API 응답 형식
- **PageResponse.java** : 페이지네이션 응답 형식 (현재 미사용)

## 인증 방식

모든 API는 JWT(JSON Web Token) 기반 인증을 사용합니다.

### 인증 헤더
```
Authorization: Bearer {access_token}
```

### 토큰 획득 방법
로그인 API(`/api/auth/signin`)를 통해 액세스 토큰을 발급받습니다.

## API 엔드포인트

### 권한 설명
- **PUBLIC**: 인증 없이 누구나 접근 가능
- **AUTHENTICATED**: 로그인한 사용자만 접근 가능
- **MEMBER**: 해당 컨테이너의 멤버(ROOT 또는 USER)만 접근 가능
- **ROOT**: 해당 컨테이너의 ROOT 권한자만 접근 가능
- **CONDITIONAL**: 조건에 따라 다름 (상세 설명 참조)

### 컨테이너 생성 및 조회
| Method | Endpoint | 설명 | 접근 권한 |
|--------|----------|------|---------|
| POST | `/api/containers` | 새 컨테이너 생성 | AUTHENTICATED |
| GET | `/api/containers/{containerId}` | 특정 컨테이너 상세 조회 | CONDITIONAL |
| GET | `/api/containers` | 접근 가능한 모든 컨테이너 조회 | AUTHENTICATED |
| GET | `/api/containers/my` | 내가 소유한 컨테이너 목록 | AUTHENTICATED |
| GET | `/api/containers/shared` | 공유받은 컨테이너 목록 | AUTHENTICATED |
| GET | `/api/containers/public` | 공개 컨테이너 목록 | PUBLIC |

### 컨테이너 관리
| Method | Endpoint | 설명 | 접근 권한 |
|--------|----------|------|--------|
| PUT | `/api/containers/{containerId}` | 컨테이너 정보 수정 | MEMBER |
| DELETE | `/api/containers/{containerId}` | 컨테이너 삭제 | ROOT |
| PUT | `/api/containers/batch/visibility` | 여러 컨테이너 공개 상태 일괄 변경 | ROOT |

### 멤버 관리
| Method | Endpoint | 설명 | 접근 권한 |
|--------|----------|------|---------|
| POST | `/api/containers/{containerId}/members` | 멤버 초대 | ROOT |
| GET | `/api/containers/{containerId}/members` | 멤버 목록 조회 | CONDITIONAL |
| DELETE | `/api/containers/{containerId}/members/{targetMemberId}` | 멤버 제거 | ROOT |
| DELETE | `/api/containers/{containerId}/members/me` | 컨테이너 탈퇴 | MEMBER |
| PUT | `/api/containers/{containerId}/members/me/activity` | 활동 시간 업데이트 | MEMBER |

### 검색 및 통계
| Method | Endpoint | 설명 | 접근 권한 |
|--------|----------|------|--------|
| GET | `/api/containers/search` | 컨테이너 검색 (간단) | PUBLIC |
| POST | `/api/containers/search` | 컨테이너 고급 검색 | PUBLIC |
| GET | `/api/containers/stats/authority` | 권한별 컨테이너 통계 | AUTHENTICATED |
| GET | `/api/containers/{containerId}/statistics` | 컨테이너 상세 통계 | MEMBER |

### 권한 상세 설명
1. **CONDITIONAL¹**: 공개 컨테이너는 누구나 조회 가능, 비공개 컨테이너는 멤버만 조회 가능
2. **PUBLIC²**: 인증 없이 접근 가능하나, 인증 시 사용자별 권한 정보가 포함됨
3. **MEMBER³**: 기본적으로 모든 멤버가 수정 가능하나, 공개 상태 변경은 ROOT만 가능
4. **ROOT⁴**: 각 컨테이너에 대해 ROOT 권한을 가진 경우만 해당 컨테이너 변경 가능
5. **MEMBER⁵**: 소유자(ROOT)는 탈퇴 불가, USER만 탈퇴 가능

## 권한 시스템

### 권한 종류
- **ROOT** : 컨테이너 소유자 (모든 권한)
  - 컨테이너 수정/삭제
  - 멤버 초대/제거
  - 공개 상태 변경
- **USER** : 일반 멤버 (제한된 권한)
  - 컨테이너 조회
  - 자신의 탈퇴
  - 활동 시간 업데이트

### 접근 권한
- **공개 컨테이너** (`containerAuth = true`) : 모든 사용자 조회 가능
- **비공개 컨테이너** (`containerAuth = false`) : 멤버만 조회 가능

## 주요 기능

### 1. 컨테이너 생성
```json
POST /api/containers
{
  "containerName": "My Project",
  "containerContent": "프로젝트 설명",
  "isPublic": true,
  "invitedMemberIds": ["user1", "user2"]
}
```

### 2. 컨테이너 검색
```json
POST /api/containers/search
{
  "name": "project",
  "isPublic": true,
  "ownerId": "owner123",
  "memberId": "member456"
}
```

### 3. 멤버 초대
```json
POST /api/containers/{containerId}/members
{
  "memberId": "newUser1"
}
```

### 4. 일괄 공개 상태 변경
```json
PUT /api/containers/batch/visibility
{
  "containerIds": [1, 2, 3],
  "isPublic": false
}
```

## 응답 형식

### HTTP 상태 코드
- **200 OK**: 성공적인 조회, 수정
- **201 Created**: 성공적인 생성
- **204 No Content**: 성공적인 삭제
- **400 Bad Request**: 잘못된 요청 (유효성 검증 실패)
- **401 Unauthorized**: 인증되지 않은 사용자
- **403 Forbidden**: 권한 없음
- **404 Not Found**: 리소스를 찾을 수 없음
- **409 Conflict**: 중복된 리소스 (예: 이미 존재하는 멤버)
- **500 Internal Server Error**: 서버 오류

### 성공 응답 (ApiResponse 사용)
```json
{
  "success": true,
  "data": {
    "containerId": 1,
    "containerName": "My Project",
    "containerContent": "프로젝트 설명",
    "isPublic": true,
    "containerDate": "2024-01-20",
    "owner": {
      "memberId": "owner123",
      "memberName": "홍길동",
      "memberEmail": "hong@example.com"
    },
    "memberCount": 3,
    "authority": "ROOT"
  },
  "message": "컨테이너가 생성되었습니다",
  "timestamp": "2024-01-20T10:00:00"
}
```

### 에러 응답 (ApiResponse 사용)
```json
{
  "success": false,
  "data": null,
  "message": "컨테이너를 찾을 수 없습니다: 999",
  "timestamp": "2024-01-20T10:00:00",
  "errorCode": "CONTAINER_NOT_FOUND",
  "path": "/api/containers/999"
}
```

## 비즈니스 규칙

1. **컨테이너 이름**
   - 1-20자 이내 (ContainerConstants.CONTAINER_NAME_MIN_LENGTH ~ CONTAINER_NAME_MAX_LENGTH)
   - 특수문자 제한 (알파벳, 숫자, 공백, 하이픈, 언더스코어만 허용)
   - 사용자별 중복 불가

2. **컨테이너 설명**
   - 최대 200자 이내 (ContainerConstants.CONTAINER_CONTENT_MAX_LENGTH)

3. **멤버 관리**
   - 소유자는 탈퇴 불가 (삭제만 가능)
   - 멤버 초대 시 최대 100명까지 일괄 초대 가능 (ContainerConstants.BATCH_OPERATION_MAX_SIZE)
   - 6개월 이상 미활동 멤버는 자동 제거 (ContainerConstants.INACTIVE_MONTHS)

4. **권한 관리**
   - 권한 종류: ROOT (관리자), USER (일반 사용자)
   - 컨테이너 생성자가 자동으로 ROOT 권한 획득
   - 초대된 멤버는 기본적으로 USER 권한
   - 권한 변경 기능은 현재 미구현

5. **공개 상태**
   - Boolean 타입 (true = 공개, false = 비공개)
   - 생성 시 설정 가능
   - ROOT 권한자만 변경 가능
   - 일괄 변경 시 최대 100개까지 처리 (ContainerConstants.BATCH_OPERATION_MAX_SIZE)

## 자동 작업

### 미활동 멤버 제거
- 실행 주기: 매일 새벽 2시 (ContainerConstants.INACTIVE_MEMBER_CLEANUP_SCHEDULE)
- 기준: 6개월 이상 미활동 (ContainerConstants.INACTIVE_MONTHS)
- 대상: 모든 컨테이너의 USER 권한 멤버 (소유자 제외)
- 구현: `@Scheduled(cron = "0 0 2 * * *")`
- 메서드: `ContainerServiceImpl.removeInactiveMembers()`

## 아키텍처 특징

### QueryDSL 통합
- 모든 복잡한 쿼리는 QueryDSL을 사용하여 타입 안전하게 구현
- 동적 쿼리 생성을 통한 유연한 검색 기능
- Fetch Join을 활용한 N+1 문제 해결

### 트랜잭션 관리
- 읽기 전용 메서드: `@Transactional(readOnly = true)`
- 쓰기 메서드: `@Transactional`
- 메서드 레벨에서 명시적 트랜잭션 경계 설정

### 보안 및 검증
- 모든 사용자 입력에 대한 null 체크
- 권한 검증을 위한 헬퍼 메서드 (`requireRootAuthority`, `hasRootAuthority`)
- 민감한 작업에 대한 이중 검증

### 코드 품질
- 중복 코드 제거를 위한 헬퍼 메서드 활용
- 상수 클래스를 통한 매직 넘버/스트링 제거
- 일관된 JavaDoc 주석 스타일

## 향후 개선 사항

1. **API 응답 일관성**
   - 모든 컨트롤러 메서드를 ApiResponse로 통합 (현재 일부만 적용)
   - GlobalExceptionHandler 구현

2. **권한 시스템 확장**
   - 권한 양도 기능
   - 세분화된 권한 레벨

3. **컨테이너 기능 확장**
   - 컨테이너 복제
   - 컨테이너 아카이브
   - 컨테이너 태그/라벨링

4. **협업 기능 강화**
   - 실시간 동시 편집
   - 멤버 간 메시징
   - 활동 로그 추적

5. **성능 최적화**
   - 페이징 기능 도입 (필요 시)
   - 캐싱 전략 도입
   - 대용량 멤버 처리 최적화

6. **문서화**
   - Swagger/OpenAPI 통합
   - API 버전 관리



