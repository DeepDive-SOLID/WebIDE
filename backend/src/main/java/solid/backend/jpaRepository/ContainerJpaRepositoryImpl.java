package solid.backend.jpaRepository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import solid.backend.entity.Container;
import solid.backend.entity.Member;
import solid.backend.entity.QContainer;
import solid.backend.entity.QTeam;
import solid.backend.entity.QTeamUser;

import java.util.List;
import java.util.Optional;

import static solid.backend.entity.QContainer.container;
import static solid.backend.entity.QTeam.team;
import static solid.backend.entity.QTeamUser.teamUser;

/**
 * 컨테이너 레포지토리 QueryDSL 구현체
 * 
 * ContainerRepositoryCustom 인터페이스에 정의된 복잡한 쿼리들을 QueryDSL로 구현합니다.
 * 
 * QueryDSL 사용 이유:
 * - 타입 안전한 쿼리 작성 (컴파일 타임에 오류 감지)
 * - 동적 쿼리 생성이 용이함
 * - 복잡한 JOIN 및 서브쿼리 처리 가능
 * - 코드 가독성이 높음
 * 
 * 주입받는 의존성:
 * - JPAQueryFactory: QueryDSL 쿼리 생성을 위한 팩토리
 *   (@Configuration 클래스에서 Bean으로 등록되어야 함)
 * 
 * @see ContainerRepositoryCustom - 구현할 인터페이스
 * @see ContainerJpaRepository - 이 구현체를 사용하는 메인 레포지토리
 */
@Repository
@RequiredArgsConstructor
public class ContainerJpaRepositoryImpl implements ContainerRepositoryCustom {
    
    /** QueryDSL 쿼리 생성을 위한 팩토리 */
    private final JPAQueryFactory queryFactory;
    
    /**
     * 컨테이너를 ID로 조회하면서 팀 정보를 Fetch Join으로 함께 가져옵니다.
     * 
     * Fetch Join 사용 이유:
     * - N+1 문제 방지: 컨테이너 조회 후 팀, 팀원 정보를 추가 쿼리 없이 한 번에 조회
     * - 성능 최적화: 1번의 쿼리로 모든 연관 데이터 로드
     * 
     * DISTINCT 사용 이유:
     * - OneToMany 관계에서 Fetch Join 사용 시 발생하는 중복 제거
     * - 컨테이너 1개 - 팀원 N명 관계에서 컨테이너가 N번 조회되는 것 방지
     */
    @Override
    public Optional<Container> findByIdWithTeam(Long containerId) {
        Container result = queryFactory
                .selectFrom(container)
                .leftJoin(container.team, team).fetchJoin()
                .leftJoin(team.teamUsers, teamUser).fetchJoin()
                .where(container.containerId.eq(containerId))
                .distinct()
                .fetchOne();
                
        return Optional.ofNullable(result);
    }
    
    /**
     * 사용자가 팀 멤버로 참여한 컨테이너 목록을 조회합니다.
     * 
     * 쿼리 로직:
     * 1. teamUser 테이블에서 현재 사용자를 찾음
     * 2. 해당 teamUser가 속한 team의 container를 조회
     * 3. 자신이 소유한 컨테이너는 제외 (owner != member)
     * 
     * 정렬: 컨테이너 생성일 기준 내림차순
     */
    @Override
    public List<Container> findSharedContainers(Member member) {
        return queryFactory
                .selectFrom(container)
                .leftJoin(container.team, team)
                .leftJoin(team.teamUsers, teamUser)
                .where(
                    teamUser.member.eq(member)
                    .and(container.owner.ne(member))
                )
                .orderBy(container.containerDate.desc())
                .distinct()
                .fetch();
    }
    
    /**
     * 사용자가 접근 가능한 모든 컨테이너를 조회합니다.
     * 
     * 접근 가능 조건 (OR 조건):
     * 1. PUBLIC 컨테이너 (isPublic = true)
     * 2. 사용자가 팀 멤버인 컨테이너
     * 
     * 중복 제거:
     * - 사용자가 팀 멤버이면서 동시에 PUBLIC인 컨테이너의 경우
     *   DISTINCT로 중복 제거
     */
    @Override
    public List<Container> findAllAccessibleContainers(Member member) {
        return queryFactory
                .selectFrom(container)
                .leftJoin(container.team, team)
                .leftJoin(team.teamUsers, teamUser)
                .where(
                    container.isPublic.isTrue()
                    .or(teamUser.member.eq(member))
                )
                .orderBy(container.containerDate.desc())
                .distinct()
                .fetch();
    }
    
    /**
     * 특정 사용자가 컨테이너의 팀 멤버인지 확인합니다.
     * 
     * 쿼리 최적화:
     * - EXISTS 대신 selectOne().fetchFirst() 사용
     * - 첫 번째 결과만 확인하여 성능 향상
     * 
     * JOIN 구조:
     * teamUser -> team -> container 순서로 조인하여
     * 특정 컨테이너의 팀에 특정 멤버가 존재하는지 확인
     */
    @Override
    public boolean isTeamMember(Long containerId, String memberId) {
        Integer count = queryFactory
                .selectOne()
                .from(teamUser)
                .join(teamUser.team, team)
                .join(container).on(container.team.eq(team))
                .where(
                    container.containerId.eq(containerId)
                    .and(teamUser.member.memberId.eq(memberId))
                )
                .fetchFirst();
                
        return count != null;
    }
    
    /**
     * 여러 컨테이너의 공개 여부를 일괄 업데이트합니다.
     * 
     * 대량 업데이트 주의사항:
     * - JPA 영속성 컨텍스트와 동기화되지 않음
     * - 영속성 컨텍스트에 있는 엔티티와 DB 상태가 달라질 수 있음
     * - 필요 시 @Modifying(clearAutomatically = true) 사용 고려
     * 
     * @return 업데이트된 레코드 수
     */
    @Override
    public long updateContainerVisibility(List<Long> containerIds, boolean isPublic) {
        return queryFactory
                .update(container)
                .set(container.isPublic, isPublic)
                .where(container.containerId.in(containerIds))
                .execute();
    }
    
    /**
     * 동적 조건에 따라 컨테이너를 검색합니다.
     * 
     * 동적 쿼리 구성:
     * - BooleanExpression을 반환하는 메서드를 사용하여 조건 구성
     * - null 값은 자동으로 무시됨 (QueryDSL의 null 처리 기능)
     * - 조건이 없으면 모든 데이터 조회
     * 
     * 하단에 정의된 헬퍼 메서드:
     * - nameContains: 컨테이너 이름 부분 일치 검색
     * - isPublicEq: 공개 여부 필터
     * - ownerIdEq: 소유자 ID 필터
     * - memberAccessible: 사용자 접근 권한 확인
     */
    @Override
    public List<Container> searchContainers(String name, Boolean isPublic, String ownerId, Member member) {
        return queryFactory
                .selectFrom(container)
                .leftJoin(container.team, team)
                .leftJoin(team.teamUsers, teamUser)
                .where(
                    nameContains(name),
                    isPublicEq(isPublic),
                    ownerIdEq(ownerId),
                    memberAccessible(member)
                )
                .orderBy(container.containerDate.desc())
                .distinct()
                .fetch();
    }
    
    /**
     * 사용자의 컨테이너별 권한 정보를 포함하여 조회합니다.
     * 
     * 조회 대상:
     * - 사용자가 소유한 컨테이너
     * - 사용자가 팀 멤버로 참여한 컨테이너
     * 
     * Fetch Join으로 함께 로드:
     * - Team 엔티티
     * - TeamUser 커렉션 (각 컨테이너에서 사용자의 권한 확인용)
     */
    @Override
    public List<Container> findContainersByMemberWithAuthority(Member member) {
        return queryFactory
                .selectFrom(container)
                .leftJoin(container.team, team).fetchJoin()
                .leftJoin(team.teamUsers, teamUser).fetchJoin()
                .where(
                    container.owner.eq(member)
                    .or(teamUser.member.eq(member))
                )
                .orderBy(container.containerDate.desc())
                .distinct()
                .fetch();
    }
    
    /*
     * 동적 쿼리를 위한 헬퍼 메서드들
     * 
     * QueryDSL의 BooleanExpression을 활용한 동적 쿼리 구성
     * - null을 반환하면 해당 조건은 쿼리에서 제외됨
     * - 메서드 체이닝을 통해 복잡한 조건 조합 가능
     * - 코드 재사용성과 가독성 향상
     */
    
    /**
     * 컨테이너 이름 포함 조건
     * 대소문자 구분 없이 부분 일치 검색 (LIKE %name%)
     */
    private BooleanExpression nameContains(String name) {
        return name != null ? container.containerName.containsIgnoreCase(name) : null;
    }
    
    /**
     * 공개 여부 필터 조건
     */
    private BooleanExpression isPublicEq(Boolean isPublic) {
        return isPublic != null ? container.isPublic.eq(isPublic) : null;
    }
    
    /**
     * 소유자 ID 필터 조건
     */
    private BooleanExpression ownerIdEq(String ownerId) {
        return ownerId != null ? container.owner.memberId.eq(ownerId) : null;
    }
    
    /**
     * 사용자 접근 권한 확인 조건
     * 
     * 접근 규칙:
     * - member == null: PUBLIC 컨테이너만 조회
     * - member != null: PUBLIC 컨테이너 + 소유 컨테이너 + 참여 컨테이너
     */
    private BooleanExpression memberAccessible(Member member) {
        if (member == null) {
            return container.isPublic.isTrue();
        }
        return container.isPublic.isTrue()
                .or(container.owner.eq(member))
                .or(teamUser.member.eq(member));
    }
}