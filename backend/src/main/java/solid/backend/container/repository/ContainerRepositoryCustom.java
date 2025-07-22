package solid.backend.container.repository;

import solid.backend.entity.Container;
import solid.backend.entity.Member;

import java.util.List;
import java.util.Optional;

/**
 * 컨테이너 레포지토리 커스텀 인터페이스
 * 
 * QueryDSL을 사용하여 복잡한 쿼리 로직을 정의하는 인터페이스입니다.
 * Spring Data JPA가 자동으로 생성할 수 없는 복잡한 쿼리들을 위해 사용됩니다.
 * 
 * 구현 패턴:
 * - 이 인터페이스는 ContainerJpaRepository가 상속합니다
 * - 실제 구현은 ContainerJpaRepositoryImpl 클래스에서 수행됩니다
 * - Spring이 자동으로 'Impl' 접미사를 가진 구현체를 찾아 주입합니다
 * 
 * 주요 기능:
 * - Fetch Join을 사용한 연관 엔티티 최적화 조회
 * - 동적 조건을 사용한 검색 쿼리
 * - 대량 데이터 업데이트
 * - 복잡한 JOIN 및 집계 쿼리
 * 
 * @see ContainerJpaRepositoryImpl - 이 인터페이스의 QueryDSL 구현체
 */
public interface ContainerRepositoryCustom {
    
    /**
     * 컨테이너를 ID로 조회하면서 팀 정보를 함께 가져옵니다.
     * 
     * Fetch Join을 사용하여 N+1 문제를 방지하고 성능을 최적화합니다.
     * Team, TeamUser, Member 엔티티를 한 번의 쿼리로 조회합니다.
     * 
     * 사용 예시:
     * - 컨테이너 상세 조회 시 팀 멤버 정보가 필요한 경우
     * - 권한 확인을 위해 팀 정보가 필요한 경우
     * 
     * @param containerId 조회할 컨테이너 ID
     * @return 팀 정보가 포함된 컨테이너 Optional 객체
     */
    Optional<Container> findByIdWithTeam(Long containerId);
    
    /**
     * 사용자가 팀 멤버로 참여한 컨테이너 목록을 조회합니다.
     * 
     * 소유한 컨테이너가 아닌, 다른 사람이 만든 컨테이너에 초대받아 참여한 경우를 조회합니다.
     * TeamUser 테이블을 통해 사용자가 속한 팀을 찾고, 해당 팀의 컨테이너를 반환합니다.
     * 
     * 조회 조건:
     * - 현재 사용자가 TeamUser로 등록된 컨테이너
     * - 컨테이너 소유자가 현재 사용자가 아닌 경우
     * 
     * @param member 현재 사용자 엔티티
     * @return 공유받은 컨테이너 목록 (생성일 기준 내림차순)
     */
    List<Container> findSharedContainers(Member member);
    
    /**
     * 사용자가 접근 가능한 모든 컨테이너를 조회합니다.
     * 
     * 접근 가능한 컨테이너의 범위:
     * 1. 사용자가 소유한 컨테이너
     * 2. 사용자가 팀 멤버로 참여한 컨테이너
     * 3. PUBLIC으로 설정된 모든 컨테이너
     * 
     * 중복 제거 및 정렬:
     * - DISTINCT를 사용하여 중복 컨테이너 제거
     * - 컨테이너 생성일 기준 내림차순 정렬
     * 
     * @param member 현재 사용자 엔티티 (null일 경우 PUBLIC 컨테이너만 조회)
     * @return 접근 가능한 컨테이너 목록
     */
    List<Container> findAllAccessibleContainers(Member member);
    
    /**
     * 특정 사용자가 컨테이너의 팀 멤버인지 확인합니다.
     * 
     * 권한 확인 시 사용됩니다:
     * - 컨테이너 접근 권한 확인
     * - 컨테이너 수정/삭제 권한 확인
     * - 팀 멤버 초대 가능 여부 확인
     * 
     * @param containerId 확인할 컨테이너 ID
     * @param memberId 확인할 사용자 ID
     * @return 팀 멤버이면 true, 아니면 false
     */
    boolean isTeamMember(Long containerId, String memberId);
    
    /**
     * 여러 컨테이너의 공개 여부를 일괄적으로 업데이트합니다.
     * 
     * 대량 컨테이너 공개 설정 변경 시 사용됩니다.
     * QueryDSL의 update 쿼리를 사용하여 한 번의 쿼리로 처리합니다.
     * 
     * 주의사항:
     * - 대량 업데이트 시 JPA 영속성 컨텍스트와 동기화되지 않음
     * - 필요 시 EntityManager.clear()를 호출하여 캐시 초기화 필요
     * 
     * @param containerIds 업데이트할 컨테이너 ID 리스트
     * @param isPublic true: PUBLIC으로 변경, false: PRIVATE로 변경
     * @return 업데이트된 컨테이너 수
     */
    long updateContainerVisibility(List<Long> containerIds, boolean isPublic);
    
    /**
     * 동적 조건에 따라 컨테이너를 검색합니다.
     * 
     * 검색 조건 (모든 조건은 AND로 결합):
     * - name: 컨테이너 이름에 포함된 문자열 (LIKE %name%)
     * - isPublic: 공개 여부 (true/false/null)
     * - ownerId: 소유자 ID
     * - member: 접근 권한 확인을 위한 현재 사용자
     * 
     * 접근 권한 규칙:
     * - PUBLIC 컨테이너: 모든 사용자 조회 가능
     * - PRIVATE 컨테이너: 소유자 또는 팀 멤버만 조회 가능
     * 
     * @param name 검색할 컨테이너 이름 (null이면 조건 제외)
     * @param isPublic 공개 여부 (null이면 조건 제외)
     * @param ownerId 소유자 ID (null이면 조건 제외)
     * @param member 현재 사용자 (null이면 PUBLIC 컨테이너만 조회)
     * @return 검색 조건에 맞는 컨테이너 목록
     */
    List<Container> searchContainers(String name, Boolean isPublic, String ownerId, Member member);
    
    /**
     * 사용자의 컨테이너별 권한 정보를 포함하여 조회합니다.
     * 
     * 컨테이너 통계 수집을 위해 사용됩니다.
     * 각 컨테이너에서 사용자의 권한 (ROOT/USER) 정보를 함께 조회합니다.
     * 
     * 조회 내용:
     * - 사용자가 소유하거나 참여한 모든 컨테이너
     * - 각 컨테이너에서의 사용자 권한 정보
     * - 팀 정보와 팀 멤버 정보
     * 
     * @param member 조회할 사용자 엔티티
     * @return 권한 정보가 포함된 컨테이너 목록
     */
    List<Container> findContainersByMemberWithAuthority(Member member);
}