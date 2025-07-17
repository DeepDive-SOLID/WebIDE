package solid.backend.jpaRepository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import solid.backend.entity.Container;
import solid.backend.entity.Member;
import solid.backend.entity.QContainer;
import solid.backend.entity.QTeam;
import solid.backend.entity.QTeamUser;
import solid.backend.entity.QAuth;
import solid.backend.common.enums.Authority;
import solid.backend.common.enums.ContainerVisibility;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 컨테이너 리포지토리 구현체
 * QueryDSL을 사용한 복잡한 쿼리 구현
 */
@Repository
@RequiredArgsConstructor
public class ContainerRepositoryImpl implements ContainerRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    // Q클래스 인스턴스
    private final QContainer container = QContainer.container;
    private final QTeam team = QTeam.team;
    private final QTeamUser teamUser = QTeamUser.teamUser;
    private final QAuth auth = QAuth.auth;
    
    @Override
    public List<Container> findAccessibleContainersByAuthority(Member member, Authority authority) {
        return queryFactory
                .selectFrom(container)
                .leftJoin(container.team, team).fetchJoin()
                .leftJoin(team.teamUsers, teamUser)
                .where(
                    container.owner.eq(member)  // 소유자이거나
                    .or(
                        teamUser.member.eq(member)  // 팀 멤버이면서
                        .and(hasMinimumAuthority(authority))  // 최소 권한을 가진 경우
                    )
                )
                .distinct()
                .fetch();
    }
    
    @Override
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
    
    @Override
    public List<Container> findContainersWithInactiveMembers(LocalDateTime inactiveDate) {
        return queryFactory
                .selectFrom(container)
                .innerJoin(container.team, team)
                .innerJoin(team.teamUsers, teamUser)
                .where(
                    teamUser.lastActivityDate.before(inactiveDate)
                    .or(teamUser.lastActivityDate.isNull())
                )
                .distinct()
                .fetch();
    }
    
    @Override
    public List<Container> searchContainers(String name, ContainerVisibility visibility, 
                                           String ownerId, String memberId) {
        BooleanBuilder builder = new BooleanBuilder();
        
        // 동적 조건 추가
        if (name != null && !name.isEmpty()) {
            builder.and(container.containerName.containsIgnoreCase(name));
        }
        
        if (visibility != null) {
            builder.and(container.containerAuth.eq(visibility == ContainerVisibility.PUBLIC));
        }
        
        if (ownerId != null && !ownerId.isEmpty()) {
            builder.and(container.owner.memberId.eq(ownerId));
        }
        
        if (memberId != null && !memberId.isEmpty()) {
            builder.and(
                container.owner.memberId.eq(memberId)
                .or(
                    container.team.teamUsers.any()
                        .member.memberId.eq(memberId)
                )
            );
        }
        
        return queryFactory
                .selectFrom(container)
                .leftJoin(container.team, team).fetchJoin()
                .where(builder)
                .distinct()
                .fetch();
    }
    
    @Override
    public List<ContainerAuthorityCount> countContainersByAuthority(Member member) {
        return queryFactory
                .select(Projections.constructor(
                    ContainerAuthorityCount.class,
                    teamUser.teamAuth.authId.castToNum(Authority.class),
                    container.count()
                ))
                .from(container)
                .innerJoin(container.team, team)
                .innerJoin(team.teamUsers, teamUser)
                .where(teamUser.member.eq(member))
                .groupBy(teamUser.teamAuth.authId)
                .fetch();
    }
    
    @Override
    public ContainerStatistics getContainerStatistics(Long containerId) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        
        return queryFactory
                .select(Projections.constructor(
                    ContainerStatistics.class,
                    teamUser.count(),
                    teamUser.lastActivityDate.max(),
                    teamUser.count().where(
                        teamUser.lastActivityDate.after(thirtyDaysAgo)
                    )
                ))
                .from(container)
                .innerJoin(container.team, team)
                .innerJoin(team.teamUsers, teamUser)
                .where(container.containerId.eq(containerId))
                .fetchOne();
    }
    
    /**
     * 최소 권한 확인을 위한 헬퍼 메서드
     */
    private BooleanExpression hasMinimumAuthority(Authority requiredAuthority) {
        // Authority enum의 ordinal 값을 사용하여 권한 계층 비교
        return teamUser.teamAuth.authId.in(
            getAuthoritiesWithMinimum(requiredAuthority)
        );
    }
    
    /**
     * 특정 권한 이상의 모든 권한 목록 반환
     */
    private List<String> getAuthoritiesWithMinimum(Authority minAuthority) {
        List<String> authorities = new java.util.ArrayList<>();
        for (Authority auth : Authority.values()) {
            if (auth.hasPermission(minAuthority)) {
                authorities.add(auth.name());
            }
        }
        return authorities;
    }
}