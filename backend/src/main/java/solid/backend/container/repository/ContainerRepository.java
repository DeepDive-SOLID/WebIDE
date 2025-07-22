package solid.backend.container.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import solid.backend.entity.*;

// QueryDSL Q클래스 import
import solid.backend.entity.QTeamUser;

// Q클래스 static import로 간결한 코드 작성
import static solid.backend.entity.QContainer.container;
import static solid.backend.entity.QTeam.team;
import static solid.backend.entity.QTeamUser.teamUser;
import static solid.backend.entity.QAuth.auth;
import static solid.backend.entity.QMember.member;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 컨테이너 리포지토리
 */
@Slf4j
@Repository
public class ContainerRepository extends QuerydslRepositorySupport {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private JPAQueryFactory queryFactory;
    
    /**
     * 생성자 - QuerydslRepositorySupport 초기화
     */
    public ContainerRepository() {
        super(Container.class);
    }
    
    /**
     * EntityManager 설정 시 QueryFactory도 함께 초기화
     * @param entityManager 엔티티 매니저
     */
    @Override
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
    
    
    /**
     * ID로 컨테이너 조회
     * @param containerId 컨테이너 ID
     * @return 컨테이너 Optional
     */
    @Transactional(readOnly = true)
    public Optional<Container> findById(Long containerId) {
        Container result = queryFactory
                .selectFrom(container)
                .where(container.containerId.eq(containerId))
                .fetchOne();
        return Optional.ofNullable(result);
    }
    
    /**
     * 컨테이너 저장 또는 업데이트
     * @param container 저장할 컨테이너
     * @return 저장된 컨테이너
     */
    @Transactional
    public Container save(Container container) {
        if (container.getContainerId() == null) {
            entityManager.persist(container);
            return container;
        } else {
            return entityManager.merge(container);
        }
    }
    
    /**
     * 컨테이너 삭제
     * @param container 삭제할 컨테이너
     */
    @Transactional
    public void delete(Container container) {
        entityManager.remove(entityManager.contains(container) ? container : entityManager.merge(container));
    }
    
    /**
     * 컨테이너 존재 여부 확인
     * @param containerId 확인할 컨테이너 ID
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long containerId) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(container)
                .where(container.containerId.eq(containerId))
                .fetchFirst();
        return fetchOne != null;
    }
    
    /**
     * 모든 컨테이너 조회
     * @return 모든 컨테이너 목록
     */
    @Transactional(readOnly = true)
    public List<Container> findAll() {
        return queryFactory
                .selectFrom(container)
                .fetch();
    }
    
    /**
     * 소유자로 컨테이너 목록 조회
     * @param owner 소유자
     * @return 소유 컨테이너 목록
     */
    @Transactional(readOnly = true)
    public List<Container> findByOwner(Member owner) {
        return queryFactory
                .selectFrom(container)
                .leftJoin(container.team, team).fetchJoin()
                .where(container.owner.eq(owner))
                .orderBy(container.containerDate.desc())
                .fetch();
    }
    
    /**
     * 공개 여부로 컨테이너 목록 조회
     * @param isPublic 공개 여부
     * @return 컨테이너 목록
     */
    public List<Container> findByIsPublic(Boolean isPublic) {
        return queryFactory
                .selectFrom(container)
                .where(container.isPublic.eq(isPublic))
                .orderBy(container.containerDate.desc())
                .fetch();
    }
    
    /**
     * 공유된 컨테이너 목록 조회
     * @param member 조회할 사용자
     * @return 공유받은 컨테이너 목록
     */
    public List<Container> findSharedContainers(Member member) {
        return queryFactory
                .selectFrom(container)
                .join(container.team, team).fetchJoin()
                .join(team.teamUsers, teamUser)
                .where(
                    teamUser.member.eq(member),
                    container.owner.ne(member)
                )
                .distinct()
                .orderBy(container.containerDate.desc())
                .fetch();
    }
    
    /**
     * 접근 가능한 모든 컨테이너 조회
     * @param member 조회할 사용자
     * @return 접근 가능한 컨테이너 목록
     */
    public List<Container> findAllAccessibleContainers(Member member) {
        return queryFactory
                .selectFrom(container)
                .leftJoin(container.team, team)
                .leftJoin(team.teamUsers, teamUser)
                .where(
                    container.owner.eq(member)
                    .or(teamUser.member.eq(member))
                )
                .distinct()
                .orderBy(container.containerDate.desc())
                .fetch();
    }
    
    /**
     * 특정 권한으로 접근 가능한 컨테이너 조회
     * @param member 조회할 사용자
     * @param authority 필요한 권한
     * @return 접근 가능한 컨테이너 목록
     */
    public List<Container> findAccessibleContainersByAuthority(Member member, String authority) {
        return queryFactory
                .selectFrom(container)
                .leftJoin(container.team, team).fetchJoin()
                .leftJoin(team.teamUsers, teamUser)
                .where(
                    container.owner.eq(member)  // 소유자는 항상 ROOT 권한
                    .or(
                        teamUser.member.eq(member)
                        .and(teamUser.teamAuth.authId.eq(authority))
                    )
                )
                .distinct()
                .fetch();
    }
    
    /**
     * 컨테이너 상세 정보 조회
     * @param containerId 조회할 컨테이너 ID
     * @return 상세 정보가 로드된 컨테이너
     */
    public Optional<Container> findContainerWithDetails(Long containerId) {
        Container result = queryFactory
                .selectFrom(container)
                .leftJoin(container.team, team).fetchJoin()
                .leftJoin(team.teamUsers, teamUser).fetchJoin()
                .leftJoin(teamUser.teamAuth, auth).fetchJoin()
                .leftJoin(container.owner).fetchJoin()
                .where(container.containerId.eq(containerId))
                .fetchOne();
                
        return Optional.ofNullable(result);
    }
    
    /**
     * 비활성 멤버가 있는 컨테이너 조회
     * @param inactiveDate 비활성 기준 날짜
     * @return 비활성 멤버가 있는 컨테이너 목록
     */
    public List<Container> findContainersWithInactiveMembers(LocalDateTime inactiveDate) {
        return queryFactory
                .selectFrom(container)
                .innerJoin(container.team, team).fetchJoin()
                .innerJoin(team.teamUsers, teamUser).fetchJoin()
                .where(
                    teamUser.lastActivityDate.before(inactiveDate)
                    .or(teamUser.lastActivityDate.isNull())
                )
                .distinct()
                .fetch();
    }
    
    /**
     * 동적 검색 조건으로 컨테이너 검색
     * @param name 컨테이너 이름
     * @param isPublic 공개 여부
     * @param ownerId 소유자 ID
     * @param memberId 참여자 ID
     * @return 검색 조건에 맞는 컨테이너 목록
     */
    public List<Container> searchContainers(String name, Boolean isPublic, 
                                           String ownerId, String memberId) {
        JPQLQuery<Container> query = queryFactory
                .selectFrom(container)
                .leftJoin(container.team, team).fetchJoin()
                .leftJoin(container.owner, member).fetchJoin();
        
        // 동적 조건 적용
        BooleanExpression predicate = buildSearchPredicate(name, isPublic, ownerId, memberId);
        if (predicate != null) {
            query.where(predicate);
        }
        
        return query.distinct().fetch();
    }
    
    /**
     * 사용자의 권한별 컨테이너 개수 조회
     * @param member 조회할 사용자
     * @return 권한별 컨테이너 개수 목록
     */
    public List<ContainerAuthorityCount> countContainersByAuthority(Member member) {
        List<ContainerAuthorityCount> result = new ArrayList<>();
        
        List<com.querydsl.core.Tuple> tuples = queryFactory
                .select(teamUser.teamAuth.authId, container.count())
                .from(container)
                .innerJoin(container.team, team)
                .innerJoin(team.teamUsers, teamUser)
                .where(teamUser.member.eq(member))
                .groupBy(teamUser.teamAuth.authId)
                .fetch();
        
        for (com.querydsl.core.Tuple tuple : tuples) {
            String authority = tuple.get(teamUser.teamAuth.authId);
            Long count = tuple.get(container.count());
            result.add(new ContainerAuthorityCount(authority, count));
        }
        
        return result;
    }
    
    /**
     * 컨테이너 상세 통계 정보 조회
     * @param containerId 조회할 컨테이너 ID
     * @return 컨테이너 통계 정보
     */
    public ContainerStatistics getContainerStatistics(Long containerId) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        
        // 전체 멤버 수 조회
        Long memberCount = queryFactory
                .select(teamUser.count())
                .from(container)
                .innerJoin(container.team, team)
                .innerJoin(team.teamUsers, teamUser)
                .where(container.containerId.eq(containerId))
                .fetchOne();
        
        // null 체크
        memberCount = memberCount != null ? memberCount : 0L;
        
        if (memberCount == 0) {
            return new ContainerStatistics(0L, null, 0L);
        }
        
        // 최근 활동 시간 조회
        LocalDateTime lastActivityDate = queryFactory
                .select(teamUser.lastActivityDate.max())
                .from(container)
                .innerJoin(container.team, team)
                .innerJoin(team.teamUsers, teamUser)
                .where(container.containerId.eq(containerId))
                .fetchOne();
        
        // 활성 멤버 수 조회 (30일 이내 활동)
        Long activeMemberCount = queryFactory
                .select(teamUser.count())
                .from(container)
                .innerJoin(container.team, team)
                .innerJoin(team.teamUsers, teamUser)
                .where(
                    container.containerId.eq(containerId),
                    teamUser.lastActivityDate.after(thirtyDaysAgo)
                )
                .fetchOne();
        
        return new ContainerStatistics(
            memberCount,
            lastActivityDate,
            activeMemberCount != null ? activeMemberCount : 0L
        );
    }
    
    /**
     * 여러 컨테이너의 공개 상태 일괄 변경
     * 
     * @param containerIds 변경할 컨테이너 ID 목록
     * @param isPublic 변경할 공개 상태
     * @return 업데이트된 컨테이너 수
     */
    @Transactional
    public long updateContainerVisibility(List<Long> containerIds, Boolean isPublic) {
        return queryFactory
                .update(container)
                .set(container.isPublic, isPublic)
                .where(container.containerId.in(containerIds))
                .execute();
    }
    
    /**
     * 비활성 컨테이너 일괄 삭제
     * @param inactiveDate 비활성 기준 날짜
     * @return 삭제된 컨테이너 수
     */
    @Transactional
    public long deleteInactiveContainers(LocalDateTime inactiveDate) {
        QTeamUser subTeamUser = new QTeamUser("subTeamUser");
        
        // 모든 멤버가 비활성인 컨테이너 ID 조회
        // (활성 멤버가 한 명도 없는 컨테이너)
        List<Long> inactiveContainerIds = queryFactory
                .select(container.containerId)
                .from(container)
                .where(
                    // 활성 멤버가 존재하지 않는 경우
                    queryFactory
                        .selectOne()
                        .from(subTeamUser)
                        .where(
                            subTeamUser.team.eq(container.team),
                            subTeamUser.lastActivityDate.goe(inactiveDate)  // 기준 날짜 이후 활동
                        )
                        .notExists()
                )
                .fetch();
        
        if (!inactiveContainerIds.isEmpty()) {
            return queryFactory
                    .delete(container)
                    .where(container.containerId.in(inactiveContainerIds))
                    .execute();
        }
        
        return 0;
    }
    
    /**
     * 특정 기간 동안 생성된 컨테이너 조회
     * 
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 기간에 생성된 컨테이너 목록
     */
    public List<Container> findContainersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return queryFactory
                .selectFrom(container)
                .where(
                    container.containerDate.between(
                        startDate.toLocalDate(), 
                        endDate.toLocalDate()
                    )
                )
                .orderBy(container.containerDate.desc())
                .fetch();
    }
    
    /**
     * 검색 조건 생성 헬퍼 메서드
     * @param name 컨테이너 이름
     * @param isPublic 공개 여부
     * @param ownerId 소유자 ID
     * @param memberId 참여자 ID
     * @return 조합된 검색 조건
     */
    private BooleanExpression buildSearchPredicate(String name, Boolean isPublic, 
                                                   String ownerId, String memberId) {
        BooleanExpression predicate = null;
        
        // 이름 검색 조건
        if (name != null && !name.isEmpty()) {
            predicate = container.containerName.containsIgnoreCase(name);
        }
        
        // 공개 여부 조건
        if (isPublic != null) {
            BooleanExpression isPublicPredicate = container.isPublic.eq(isPublic);
            predicate = predicate == null ? isPublicPredicate : predicate.and(isPublicPredicate);
        }
        
        // 소유자 조건
        if (ownerId != null && !ownerId.isEmpty()) {
            BooleanExpression ownerPredicate = container.owner.memberId.eq(ownerId);
            predicate = predicate == null ? ownerPredicate : predicate.and(ownerPredicate);
        }
        
        // 참여자 조건 (소유자 또는 팀 멤버)
        if (memberId != null && !memberId.isEmpty()) {
            BooleanExpression memberPredicate = container.owner.memberId.eq(memberId)
                    .or(
                        queryFactory
                            .selectOne()
                            .from(teamUser)
                            .where(
                                teamUser.team.eq(container.team),
                                teamUser.member.memberId.eq(memberId)
                            )
                            .exists()
                    );
            predicate = predicate == null ? memberPredicate : predicate.and(memberPredicate);
        }
        
        return predicate;
    }
    
    /**
     * 권한별 컨테이너 개수 DTO
     * 사용자가 가진 권한별로 컨테이너 개수를 집계하기 위한 클래스
     */
    public static class ContainerAuthorityCount {
        /** 권한 타입 (ROOT, USER) */
        private final String authority;
        /** 해당 권한으로 접근 가능한 컨테이너 개수 */
        private final Long count;
        
        public ContainerAuthorityCount(String authority, Long count) {
            this.authority = authority;
            this.count = count;
        }
        
        public String getAuthority() { return authority; }
        public Long getCount() { return count; }
    }
    
    /**
     * 컨테이너 통계 정보 DTO
     * 특정 컨테이너의 멤버 활동 통계를 담는 클래스
     */
    public static class ContainerStatistics {
        /** 전체 멤버 수 */
        private final Long memberCount;
        /** 가장 최근 멤버 활동 시간 */
        private final LocalDateTime lastActivityDate;
        /** 활성 멤버 수 (30일 이내 활동) */
        private final Long activeMemberCount;
        
        public ContainerStatistics(Long memberCount, LocalDateTime lastActivityDate, Long activeMemberCount) {
            this.memberCount = memberCount;
            this.lastActivityDate = lastActivityDate;
            this.activeMemberCount = activeMemberCount;
        }
        
        public Long getMemberCount() { return memberCount; }
        public LocalDateTime getLastActivityDate() { return lastActivityDate; }
        public Long getActiveMemberCount() { return activeMemberCount; }
    }
}