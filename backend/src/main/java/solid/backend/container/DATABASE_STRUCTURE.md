# Container Module Database Structure Guide

이 문서는 데이터베이스 구조와 API 응답 구조의 차이점을 설명합니다. 팀 개발자들이 혼동하지 않도록 작성되었습니다.

## 📊 데이터베이스 구조 vs API 응답 구조

### 1. Owner 정보 처리

#### 데이터베이스 구조 (Entity)
```java
// Container.java
@Entity
@Table(name = "container")
public class Container {
    // ... 다른 필드들
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Member owner;  // Member 엔티티 전체를 참조
}
```

**실제 DB 테이블**:
```sql
CREATE TABLE container (
    container_id BIGINT PRIMARY KEY,
    container_nm VARCHAR(20) NOT NULL,
    container_content VARCHAR(200),
    is_public BOOLEAN NOT NULL,
    container_date DATE NOT NULL,
    owner_id VARCHAR(255) NOT NULL,  -- Member 테이블의 FK
    team_id BIGINT NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES member(member_id)
);
```

#### API 응답 구조 (DTO)
```java
// ContainerResponseDto.java
public class ContainerResponseDto {
    private String ownerName;    // 소유자 이름만
    private String ownerId;      // 소유자 ID만
    // owner 객체를 사용하지 않음!
}
```

**실제 API 응답**:
```json
{
  "containerId": 1,
  "containerName": "My Project",
  "ownerName": "홍길동",      // 평탄화된 구조
  "ownerId": "user123",       // 평탄화된 구조
  "memberCount": 3
}
```

### 2. 왜 이렇게 다른가?

#### 데이터베이스에서
- **정규화**: owner_id를 FK로 저장하여 데이터 중복 방지
- **관계 매핑**: JPA/Hibernate가 자동으로 Member 엔티티 조회
- **Lazy Loading**: 필요할 때만 Member 정보 로드

#### API 응답에서
- **단순화**: 클라이언트는 소유자의 이름과 ID만 필요
- **성능**: 불필요한 데이터 전송 방지
- **보안**: 민감한 정보(비밀번호, 개인정보 등) 노출 방지

### 3. 변환 과정

```java
// ContainerResponseDto.from() 메서드
public static ContainerResponseDto from(Container container, ...) {
    return ContainerResponseDto.builder()
        // DB에서는 owner 객체지만, API에서는 평탄화
        .ownerName(container.getOwner().getMemberName())
        .ownerId(container.getOwner().getMemberId())
        .build();
}
```

## ⚠️ 주의사항

### 1. 쿼리 작성 시
```java
// ❌ 잘못된 예 - N+1 문제 발생
List<Container> containers = containerRepository.findAll();
// 각 container.getOwner() 호출 시 추가 쿼리 발생

// ✅ 올바른 예 - Fetch Join 사용
return queryFactory
    .selectFrom(container)
    .leftJoin(container.owner, member).fetchJoin()  // owner 정보 함께 조회
    .fetch();
```

### 2. DTO 변환 시
```java
// ❌ 잘못된 예 - owner 객체를 그대로 노출
{
  "owner": {
    "memberId": "user123",
    "memberName": "홍길동",
    "memberPassword": "xxx",  // 보안 위험!
    "memberEmail": "xxx"      // 불필요한 정보
  }
}

// ✅ 올바른 예 - 필요한 정보만 평탄화
{
  "ownerName": "홍길동",
  "ownerId": "user123"
}
```

## 📝 개발 가이드

### 1. 새로운 API 개발 시
1. **Entity는 DB 구조를 그대로 반영**
   - 관계 매핑 사용 (@ManyToOne, @OneToMany 등)
   - 정규화 원칙 준수

2. **DTO는 클라이언트 needs에 맞춤**
   - 필요한 필드만 포함
   - 가능한 평탄한 구조 유지
   - 민감한 정보 제외

### 2. 성능 고려사항
```java
// Repository에서 필요한 연관 데이터는 미리 조회
@Query("SELECT c FROM Container c " +
       "LEFT JOIN FETCH c.owner " +
       "LEFT JOIN FETCH c.team t " +
       "LEFT JOIN FETCH t.teamUsers " +
       "WHERE c.containerId = :id")
Optional<Container> findContainerWithDetails(@Param("id") Long id);
```

### 3. 팀 협업 시
- **백엔드 개발자**: Entity 구조 변경 시 DTO 변환 로직도 함께 수정
- **프론트엔드 개발자**: API 문서의 응답 구조만 참조 (DB 구조는 몰라도 됨)
- **DBA**: Entity의 @Table, @Column 어노테이션 참조

## 🔍 자주 하는 실수

### 1. Entity를 그대로 반환
```java
// ❌ 절대 하지 마세요!
@GetMapping("/{id}")
public Container getContainer(@PathVariable Long id) {
    return containerRepository.findById(id).orElseThrow();
}
// 문제: 순환 참조, 민감한 정보 노출, 불필요한 데이터 전송
```

### 2. DTO에서 Entity 관계 유지
```java
// ❌ DTO에서 다른 DTO 참조
public class ContainerResponseDto {
    private OwnerDto owner;  // 중첩 구조 피하기
}

// ✅ 평탄화
public class ContainerResponseDto {
    private String ownerName;
    private String ownerId;
}
```

## 📌 요약

| 항목 | Database (Entity) | API Response (DTO) | 이유 |
|------|------------------|-------------------|------|
| Owner 정보 | Member 객체 참조 | ownerName, ownerId 필드 | 단순화, 보안 |
| 관계 표현 | @ManyToOne, @OneToMany | 평탄한 필드 | 성능, 사용성 |
| 필드명 | container_auth | isPublic | 직관성 |
| 데이터 범위 | 모든 관련 데이터 | 필요한 데이터만 | 효율성 |

**핵심**: DB는 정규화와 관계를 중시, API는 사용성과 효율성을 중시!