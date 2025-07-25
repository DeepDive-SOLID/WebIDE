### 컨테이너 관리 : container
- controller(컨트롤러)
    - ContainerController.java
- dto(객체정보)
    - BatchContainerVisibilityDto.java
    - ContainerCreateDto.java
    - ContainerMemberDto.java
    - ContainerResponseDto.java
    - ContainerSearchDto.java
    - ContainerSearchResponseDto.java
    - ContainerStatisticsDto.java
    - ContainerUpdateDto.java
    - GroupMemberResponseDto.java
    - MemberInviteDto.java
- repository(jpa)
    - ContainerJpaRepository.java
    - ContainerQueryRepository.java
- service(비즈니스 로직)
    - ContainerService.java
    - ContainerServiceImpl.java
- exception(예외처리)
    - ContainerException.java
    - ContainerNotFoundException.java
    - DuplicateMemberException.java
    - InvalidMemberException.java
    - MemberNotFoundException.java
    - UnauthorizedContainerAccessException.java
- constant(상수)
    - ContainerConstants.java
- common(공통)
    - ApiResponse.java

### API 목록
[컨테이너 생성]
- HTTP method : POST
- HTTP request URL : /api/containers
- param : ContainerCreateDto
- return : ApiResponse<ContainerResponseDto>

[컨테이너 조회]
- HTTP method : GET
- HTTP request URL : /api/containers/{containerId}
- return : ApiResponse<ContainerResponseDto>

[컨테이너 수정]
- HTTP method : PUT
- HTTP request URL : /api/containers/{containerId}
- param : ContainerUpdateDto
- return : ApiResponse<ContainerResponseDto>

[컨테이너 삭제]
- HTTP method : DELETE
- HTTP request URL : /api/containers/{containerId}
- return : ApiResponse<Void>

[내 컨테이너 목록 조회]
- HTTP method : GET
- HTTP request URL : /api/containers/my
- return : ApiResponse<List<ContainerResponseDto>>

[컨테이너 검색]
- HTTP method : GET
- HTTP request URL : /api/containers/search
- param : containerName, isPublic, ownerId (Query Params)
- return : ApiResponse<List<ContainerSearchResponseDto>>

[컨테이너 고급 검색]
- HTTP method : POST
- HTTP request URL : /api/containers/search/advanced
- param : ContainerSearchDto
- return : ApiResponse<List<ContainerSearchResponseDto>>

[컨테이너 통계 조회]
- HTTP method : GET
- HTTP request URL : /api/containers/{containerId}/statistics
- return : ApiResponse<ContainerStatisticsDto>

[권한별 컨테이너 수 통계]
- HTTP method : GET
- HTTP request URL : /api/containers/stats/authority
- return : ApiResponse<Map<String, Integer>>

[멤버 초대]
- HTTP method : POST
- HTTP request URL : /api/containers/{containerId}/members
- param : MemberInviteDto
- return : ApiResponse<List<GroupMemberResponseDto>>

[멤버 제거]
- HTTP method : DELETE
- HTTP request URL : /api/containers/{containerId}/members/{memberId}
- return : ApiResponse<Void>

[멤버 권한 변경] - 미구현
- HTTP method : PUT
- HTTP request URL : /api/containers/{containerId}/members/{memberId}/authority
- param : authority (Query Param)
- return : ApiResponse<GroupMemberResponseDto>
- 현재 구현되지 않음

[컨테이너 공개 상태 일괄 변경]
- HTTP method : PUT
- HTTP request URL : /api/containers/batch/visibility
- param : BatchContainerVisibilityDto
- return : ApiResponse<List<ContainerResponseDto>>

### 주요 기능

#### 기본 기능 (MVP)
- 컨테이너 생성/수정/삭제 (CRUD)
- 멤버 초대 및 제거
- 공개/비공개 설정
- 내 컨테이너 목록 조회
- 비활성 멤버 자동 제거 : 6개월 이상 미활동 시 자동 탈퇴 (스케줄러)
- 활동 시간 추적 : 컨테이너 접근 시 자동으로 lastActivityDate 업데이트

#### 추가 구현 기능
- **컨테이너 검색** : 이름, 공개여부, 소유자로 검색
- **고급 검색** : 멤버 수, 생성일 범위, 정렬 옵션 포함
- **통계 조회** : 멤버 목록, 접속 상태, 가입일, 활동 시간 표시
- **권한별 통계** : 사용자가 ROOT/USER 권한을 가진 컨테이너 수 집계
- **배치 공개 상태 변경** : 소유한 여러 컨테이너를 선택하여 한 번에 공개/비공개로 변경

### 권한 시스템
- **ROOT (소유자 권한)**
  - 컨테이너 정보 수정/삭제
  - 멤버 초대/제거
  - 공개 상태 변경
  - 모든 파일 및 코드 접근
  
- **USER (일반 멤버 권한)**
  - 컨테이너 조회
  - 모든 파일 및 코드 접근
  - 코드 실행
  - 자신의 탈퇴
  - 활동 시간 업데이트

### 접근 권한
- **공개 컨테이너** (`isPublic = true`) : 모든 사용자 조회 가능
- **비공개 컨테이너** (`isPublic = false`) : 멤버만 조회 가능

### 엔티티 관계
- Container 1:1 Team (컨테이너별 팀 자동 생성)
- Team 1:N TeamUser (팀 멤버 관리)
- TeamUser N:1 Member (사용자 정보)
- TeamUser N:1 Auth (권한 정보)

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

모든 API는 통일된 ApiResponse 형식을 사용합니다.

### HTTP 상태 코드
- **200 OK**: 성공적인 조회, 수정
- **201 Created**: 성공적인 생성
- **400 Bad Request**: 잘못된 요청 (유효성 검증 실패)
- **401 Unauthorized**: 인증되지 않은 사용자
- **403 Forbidden**: 권한 없음
- **404 Not Found**: 리소스를 찾을 수 없음
- **409 Conflict**: 중복된 리소스 (예: 이미 존재하는 멤버)
- **500 Internal Server Error**: 서버 오류

### 성공 응답
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
    "ownerId": "owner123",
    "memberCount": 3,
    "userAuthority": "ROOT"
  },
  "message": "컨테이너가 생성되었습니다",
  "timestamp": "2024-01-20T10:00:00"
}
```

### 에러 응답
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

### ApiResponse 필드 설명
- **success**: 요청 성공 여부 (boolean)
- **data**: 실제 응답 데이터 (성공 시)
- **message**: 사용자에게 표시할 메시지 (선택적)
- **timestamp**: 응답 시간
- **errorCode**: 에러 코드 (실패 시, 선택적)
- **path**: 요청 경로 (실패 시, 선택적)

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

### 실시간 접속 상태 관리 (예정)
- 현재 컨테이너 통계 API에서 활성/비활성 멤버 수는 모두 0으로 표시
- WebSocket 기반 실시간 접속 상태 관리 기능 구현 예정
- Redis 또는 메모리 기반 접속 상태 추적

## 통합 기능

### 도커 컨테이너 실행 환경
- 각 컨테이너별로 격리된 코드 실행 환경 제공
- 지원 언어: Python, Java, JavaScript, C, C++
- 리소스 제한: 메모리 512MB, CPU 0.5 Core, 실행 시간 30초
- 보안: 위험한 시스템 명령어 차단, 파일/네트워크 접근 제한
- API: `/api/docker/*` 엔드포인트를 통한 코드 실행

### 파일 관리 시스템
- 컨테이너별 독립적인 파일 저장소
- 파일 업로드/다운로드, 디렉토리 관리
- 저장 경로: `{user.home}/Downloads/solid/container-{id}/`
- 최대 파일 크기: 10MB

## 아키텍처 특징

### 레포지토리 계층
- **ContainerJpaRepository**: Spring Data JPA 기본 CRUD 레포지토리
- **ContainerQueryRepository**: QueryDSL 전용 독립 레포지토리 (복잡한 쿼리 처리)

### QueryDSL 통합
- 모든 복잡한 쿼리는 QueryDSL을 사용하여 타입 안전하게 구현
- 동적 쿼리 생성을 통한 유연한 검색 기능
- Fetch Join을 활용한 N+1 문제 해결
- BooleanExpression을 활용한 동적 조건 처리

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
- 상세한 메서드 레벨 주석 (매개변수, 반환값, 예외, 사용 예시)

## 향후 개선 사항

1. **API 응답 일관성**
   - ✓ 모든 컨테이너 컨트롤러 메서드를 ApiResponse로 통합 완료 (2025-01-21)
   - GlobalExceptionHandler 구현 필요

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
   - WebSocket 기반 실시간 접속 상태 관리

5. **성능 최적화**
   - 페이징 기능 도입 (필요 시)
   - 캐싱 전략 도입
   - 대용량 멤버 처리 최적화

6. **문서화**
   - Swagger/OpenAPI 통합
   - API 버전 관리

## 최근 변경사항

### 2025-07-21
1. **API 응답 형식 통일**
   - 모든 컨테이너 API 응답을 ApiResponse 형식으로 통일
   - 성공/실패 응답 형식 표준화
   - 모든 응답에 timestamp 필드 추가
   - 상황에 따른 메시지 필드 활용
   - API 문서 및 HTTP 테스트 파일 업데이트

### 2025-07-21  
1. **레포지토리 구조 개선**
   - QueryDSL 독립 레포지토리 패턴 적용
   - ContainerQueryRepository로 QueryDSL 쿼리 분리
   - 최신 Spring Data JPA 패턴 적용

2. **코드 품질 향상**
   - 모든 레포지토리 메서드에 상세 JavaDoc 추가
   - QueryDSL 구현체에 성능 및 주의사항 주석 추가

3. **실시간 접속 상태 기능 준비**
   - ContainerStatisticsDto에 활성/비활성 멤버 필드 업데이트
   - WebSocket 구현 대기 중 (TODO 주석 추가)

4. **기타 개선사항**
   - 중복 ContainerStatistics.java 파일 삭제
   - ContainerUpdateDto에 상수 사용
   - 컨테이너 컨트롤러 및 예외 처리기 주석 개선
