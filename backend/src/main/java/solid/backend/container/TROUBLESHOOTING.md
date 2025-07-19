# Container Module Troubleshooting Guide

이 문서는 컨테이너 모듈 개발 및 운영 중 발생할 수 있는 문제들과 해결 방법을 정리한 가이드입니다.

## 목차
1. [컴파일 오류](#컴파일-오류)
2. [런타임 오류](#런타임-오류)
3. [API 호출 오류](#api-호출-오류)
4. [데이터베이스 관련 문제](#데이터베이스-관련-문제)
5. [권한 관련 문제](#권한-관련-문제)
6. [성능 문제](#성능-문제)
7. [개발 과정 이슈](#개발-과정-이슈)

---

## 컴파일 오류

### 1. QueryDSL Q클래스를 찾을 수 없음
**증상**
```
error: cannot find symbol
import static solid.backend.entity.QContainer.container;
```

**원인**
- QueryDSL 어노테이션 프로세서가 실행되지 않음
- Q클래스가 생성되지 않음

**해결방법**
```bash
# Maven clean build
mvn clean compile

# Gradle clean build
./gradlew clean build

# IntelliJ에서
# 1. Build > Rebuild Project
# 2. File > Invalidate Caches and Restart
```

### 2. Enum 타입 관련 컴파일 오류
**증상**
```
error: cannot find symbol
ContainerVisibility.PUBLIC
```

**원인**
- Enum을 제거하고 primitive 타입으로 변경했지만 일부 코드가 남아있음

**해결방법**
- `ContainerVisibility` → `Boolean` (isPublic)
- `Authority` → `String` ("ROOT", "USER")
- ContainerConstants 상수 사용

---

## 런타임 오류

### 1. NullPointerException - 멤버 ID가 null
**증상**
```
java.lang.NullPointerException: Member ID cannot be null
```

**원인**
- JWT 토큰이 없거나 만료됨
- SecurityContext에서 인증 정보를 가져오지 못함

**해결방법**
```java
// ContainerServiceImpl에 추가된 null 체크
validateMemberId(memberId, ERROR_MEMBER_ID_REQUIRED);

// Controller에서 확인
String memberId = getCurrentMemberId();
if (memberId == null) {
    throw new UnauthorizedException("인증이 필요합니다");
}
```

### 2. LazyInitializationException
**증상**
```
org.hibernate.LazyInitializationException: could not initialize proxy
```

**원인**
- 트랜잭션 범위를 벗어난 상태에서 lazy loading 시도
- Fetch Join을 사용하지 않음

**해결방법**
```java
// ContainerRepository에서 Fetch Join 사용
return queryFactory
    .selectFrom(container)
    .leftJoin(container.team, team).fetchJoin()
    .leftJoin(team.teamUsers, teamUser).fetchJoin()
    .where(container.containerId.eq(containerId))
    .fetchOne();
```

### 3. 트랜잭션 읽기 전용 오류
**증상**
```
org.springframework.dao.TransientDataAccessResourceException: 
Connection is read-only. Queries leading to data modification are not allowed
```

**원인**
- 클래스 레벨 `@Transactional(readOnly = true)` 설정
- 쓰기 작업 메서드에 별도 트랜잭션 설정 없음

**해결방법**
```java
// 쓰기 메서드에 명시적으로 설정
@Override
@Transactional  // readOnly = false가 기본값
public ContainerResponseDto createContainer(...) {
    // ...
}
```

---

## API 호출 오류

### 1. 400 Bad Request - 유효성 검증 실패
**증상**
```json
{
  "success": false,
  "message": "입력값 검증에 실패했습니다",
  "errorCode": "VALIDATION_FAILED",
  "data": {
    "containerName": "컨테이너 이름은 1~20자여야 합니다"
  }
}
```

**원인**
- 요청 필드가 validation 규칙에 맞지 않음

**해결방법**
- containerName: 1-20자, 특수문자는 하이픈과 언더스코어만
- containerContent: 최대 200자
- isPublic: Boolean 필수값

### 2. 401 Unauthorized
**증상**
```json
{
  "success": false,
  "message": "인증이 필요합니다",
  "errorCode": "UNAUTHORIZED"
}
```

**원인**
- Authorization 헤더 누락
- 토큰 만료 또는 잘못된 형식

**해결방법**
```bash
# 올바른 헤더 형식
Authorization: Bearer {access_token}

# 토큰 재발급
POST /api/auth/signin
```

### 3. 403 Forbidden - 권한 없음
**증상**
```json
{
  "success": false,
  "message": "ROOT 권한이 필요합니다.",
  "errorCode": "UNAUTHORIZED_ACCESS"
}
```

**원인**
- 해당 작업에 필요한 권한이 없음
- ROOT 권한이 필요한 작업을 USER가 시도

**해결방법**
- 컨테이너 삭제, 멤버 초대/제거는 ROOT만 가능
- 자신의 권한 확인: GET /api/containers/{id}의 userAuthority 필드

### 4. 404 Not Found
**증상**
```json
{
  "success": false,
  "message": "컨테이너를 찾을 수 없습니다: 123",
  "errorCode": "CONTAINER_NOT_FOUND"
}
```

**원인**
- 존재하지 않는 컨테이너 ID
- 비공개 컨테이너에 멤버가 아닌 사용자가 접근

**해결방법**
- 컨테이너 ID 확인
- 접근 권한 확인 (공개 여부, 멤버 여부)

---

## 데이터베이스 관련 문제

### 1. Unique Constraint Violation
**증상**
```
java.sql.SQLIntegrityConstraintViolationException: 
Duplicate entry 'container_name' for key 'UK_container_name_owner'
```

**원인**
- 같은 사용자가 동일한 이름의 컨테이너 생성 시도

**해결방법**
- 컨테이너 이름 변경
- 기존 컨테이너 삭제 후 재생성

### 2. Foreign Key Constraint Failure
**증상**
```
Cannot delete or update a parent row: a foreign key constraint fails
```

**원인**
- 연관된 데이터가 있는 상태에서 삭제 시도

**해결방법**
- Cascade 옵션 확인
- 연관 데이터 먼저 삭제

### 3. N+1 쿼리 문제
**증상**
- 컨테이너 목록 조회 시 성능 저하
- 많은 수의 SELECT 쿼리 발생

**원인**
- Lazy Loading으로 인한 추가 쿼리 발생

**해결방법**
```java
// Fetch Join 사용
.leftJoin(container.team, team).fetchJoin()
.leftJoin(team.teamUsers, teamUser).fetchJoin()
```

---

## 권한 관련 문제

### 1. 소유자를 제거하려고 할 때
**증상**
```json
{
  "message": "소유자는 제거할 수 없습니다."
}
```

**해결방법**
- 소유자는 제거 불가, 컨테이너 자체를 삭제해야 함
- 다른 멤버에게 소유권 이전 기능은 미구현

### 2. 비공개 컨테이너 접근 불가
**증상**
```json
{
  "message": "비공개 컨테이너에 접근할 수 없습니다"
}
```

**해결방법**
- 컨테이너 멤버로 초대받아야 함
- 공개 상태로 변경 (ROOT 권한 필요)

---

## 성능 문제

### 1. 대량의 컨테이너 조회 시 느림
**원인**
- 전체 목록을 한 번에 조회
- 불필요한 데이터 fetch

**해결방법**
```java
// 프로젝션 사용으로 필요한 필드만 조회
.select(Projections.bean(ContainerResponseDto.class,
    container.containerId,
    container.containerName,
    // ...
))

// 조회 조건 추가로 결과 제한
.where(container.containerDate.after(dateLimit))
.limit(100)  // 결과 개수 제한
```

### 2. 비활동 멤버 정리 스케줄러 부하
**증상**
- 매일 새벽 2시 서버 부하 증가

**원인**
- 모든 컨테이너를 순회하며 멤버 확인

**해결방법**
- 배치 처리로 분할 실행
- 인덱스 추가: lastActivityDate

---

## 개발 과정 이슈

### 1. Enum 제거 후 하드코딩된 문자열
**문제점**
- "ROOT", "USER" 등이 여러 곳에 하드코딩

**해결방법**
```java
// ContainerConstants 사용
import static solid.backend.container.constant.ContainerConstants.*;

if (authority.equals(AUTHORITY_ROOT)) {
    // ...
}
```

### 2. 중복 코드 패턴
**문제점**
- 동일한 검증 로직이 여러 메서드에 반복

**해결방법**
```java
// 헬퍼 메서드 추가됨
private void requireRootAuthority(Container container, String memberId, String errorMessage)
private void validateMemberId(String memberId, String errorMessage)
private List<ContainerResponseDto> convertToResponseDtoList(List<Container> containers, String memberId)
private TeamUser createTeamUser(Team team, Member member, Auth auth)
```

### 3. API 응답 일관성
**문제점**
- 일부 API만 ApiResponse 사용
- 나머지는 직접 DTO 반환

**해결방법**
```java
// 향후 모든 Controller 메서드를 ApiResponse로 통일 필요
return ResponseEntity.ok(ApiResponse.success(data, "성공 메시지"));
```

---


## 디버깅 팁

### 1. SQL 로그 활성화
```yaml
# application.yml
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### 2. QueryDSL 쿼리 로그
```java
// QueryDSL 쿼리를 로그로 출력
JPAQuery<?> query = queryFactory.selectFrom(container);
log.debug("Query: {}", query);
```

### 3. 트랜잭션 범위 확인
```java
// 트랜잭션 상태 로그
log.debug("Transaction active: {}", TransactionSynchronizationManager.isActualTransactionActive());
log.debug("Transaction read-only: {}", TransactionSynchronizationManager.isCurrentTransactionReadOnly());
```


