# Container Module Changelog

이 문서는 컨테이너 모듈의 주요 변경 사항을 기록합니다.

## [2.0.0] - 2025-07-19

### 🚨 Breaking Changes
- **Enum 제거 및 Primitive 타입 변경**
  - `Authority` enum → `String` 타입 ("ROOT", "USER")
  - `ContainerVisibility` enum → `Boolean` 타입 (`isPublic`)
  - 모든 API 요청/응답에서 enum 필드가 primitive 타입으로 변경됨


- **DTO 필드명 변경**
  - `description` → `containerContent`
  - `memberIds` → `invitedMemberIds`
  - `authority` → `userAuthority`
  - `containerAuth` → `isPublic`

- **응답 구조 변경**
  - owner 객체 구조 평탄화:
    ```json
    // 변경 전 (원래 owner 객체를 사용하지 않았음)
    "owner": {
      "memberId": "user123",
      "memberName": "홍길동"
    }
    
    // 변경 후 (DB는 owner_id FK, API는 평탄화)
    "ownerName": "홍길동",
    "ownerId": "user123"
    ```
    **중요**: 데이터베이스에서는 `owner_id` FK로 저장되지만, API 응답에서는 평탄화된 구조 사용

### ✨ New Features
- **상수 클래스 추가** (`ContainerConstants.java`)
  - 권한 타입 상수: `AUTHORITY_ROOT`, `AUTHORITY_USER`
  - 제한 사항 상수: 이름/설명 길이, 배치 작업 크기 등
  - 에러 메시지 상수: 자주 사용되는 에러 메시지 표준화
  - 스케줄러 설정 상수

- **새로운 DTO 추가**
  - `BatchContainerVisibilityDto`: 일괄 공개 상태 변경 요청
  - `ContainerSearchDto`: 고급 검색 조건 DTO

- **통합 API 응답 형식**
  - `ApiResponse`: 모든 API 응답을 위한 통합 래퍼 클래스
  - `PageResponse`: 페이지네이션 응답 형식 (현재 미사용)

- **문서화 추가**
  - `README.md`: 모듈 개요 및 사용 가이드
  - `API_REFERENCE.md`: 상세 API 명세
  - `TROUBLESHOOTING.md`: 문제 해결 가이드
  - `DATABASE_STRUCTURE.md`: DB 구조와 API 응답 차이 설명
  - `CHANGELOG.md`: 변경 이력 (이 문서)

### 🔧 Improvements
- **Repository 통합**
  - 3개 파일 통합: `ContainerRepository` + `ContainerRepositoryCustom` + `ContainerRepositoryImpl` → `ContainerRepository`
  - 모든 쿼리를 QueryDSL 기반으로 재작성
  - Fetch Join을 활용한 N+1 문제 해결

- **보안 및 검증 강화**
  - 모든 사용자 입력에 대한 null 체크 추가
  - 매직 스트링을 상수로 교체
  - 권한 검증 로직 통합 (`requireRootAuthority`, `hasRootAuthority`)

- **트랜잭션 관리 개선**
  - 클래스 레벨 `@Transactional(readOnly = true)` 제거
  - 메서드 레벨에서 명시적 트랜잭션 경계 설정
  - 읽기/쓰기 작업에 따른 적절한 트랜잭션 설정

- **코드 품질 개선**
  - 중복 코드 제거를 위한 헬퍼 메서드 추가:
    - `requireRootAuthority()`: ROOT 권한 검증
    - `validateMemberId()`: 멤버 ID 유효성 검증
    - `convertToResponseDtoList()`: 컨테이너 리스트 변환
    - `createTeamUser()`: TeamUser 생성 로직 통합
  - JavaDoc 주석 표준화 (간결한 형식)
  - 일관된 코딩 스타일 적용


- **QueryDSL 쿼리 수정**
  - deprecated `fetchCount()` → `count()` 쿼리 분리
  - `deleteInactiveContainers` 로직 오류 수정
  - 잘못된 where 절 위치 수정

- **예외 처리 개선**
  - `DuplicateMemberException` 추가
  - 미사용 예외 클래스 제거
  - 에러 메시지 일관성 개선

### 📝 Documentation
- **README.md 추가 내용**
  - JWT 인증 방식 설명
  - HTTP 상태 코드 정의
  - 권한 시스템 명확화 (PUBLIC, AUTHENTICATED, MEMBER, ROOT)
  - 아키텍처 특징 섹션 추가

- **API_REFERENCE.md 수정**
  - 모든 응답 예제를 실제 DTO 구조에 맞게 수정
  - 자동 작업 (스케줄러) 섹션 추가
  - ContainerConstants 상수 정의 섹션 추가
  - 검증 규칙 업데이트 (1-20자, 200자 제한 등)

### 🗑️ Removed
- **삭제된 파일**
  - `/common/enums/Authority.java`
  - `/common/enums/ContainerVisibility.java`
  - `/container/dto/ApiResponse.java` (common으로 이동)
  - `/jpaRepository/ContainerRepositoryCustom.java`
  - `/jpaRepository/ContainerRepositoryImpl.java`
  - 미사용 예외 클래스들

- **제거된 기능**
  - enum 기반 권한/가시성 시스템

### 📋 Migration Guide

#### 1. API 요청 변경
```java
// 변경 전
{
  "containerName": "프로젝트",
  "description": "설명",
  "visibility": "PUBLIC",
  "memberIds": ["user1"]
}

// 변경 후
{
  "containerName": "프로젝트",
  "containerContent": "설명",
  "isPublic": true,
  "invitedMemberIds": ["user1"]
}
```

#### 2. 응답 처리 변경
```java
// 변경 전
if (response.getAuthority() == Authority.ROOT) { }
if (response.getContainerAuth() == ContainerVisibility.PUBLIC) { }

// 변경 후
if ("ROOT".equals(response.getUserAuthority())) { }
if (Boolean.TRUE.equals(response.getIsPublic())) { }
```


### 🔜 Future Plans
- GlobalExceptionHandler 구현
- 모든 컨트롤러 메서드를 ApiResponse로 통합
- Swagger/OpenAPI 문서화
- 권한 양도 기능
- 컨테이너 복제 기능
- 캐싱 전략 도입

---

## [1.0.0] - 2025-07-17

### Initial Release
- 컨테이너 CRUD 기능
- 멤버 관리 (초대, 제거, 탈퇴)
- 권한 시스템 (ROOT, USER)
- 공개/비공개 설정
- 검색 기능
- 통계 조회

---

## Version History

| Version | Date       | Description |
|---------|------------|-------------|
| 2.0.0 | 2025-07-19 | 대규모 리팩토링: Enum 제거, Repository 통합 |
| 1.0.0 | 2025-07-17 | 초기 릴리즈 |