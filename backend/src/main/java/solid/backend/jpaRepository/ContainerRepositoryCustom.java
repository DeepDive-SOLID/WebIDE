package solid.backend.jpaRepository;

import solid.backend.entity.Container;
import solid.backend.entity.Member;
import solid.backend.common.enums.Authority;
import solid.backend.common.enums.ContainerVisibility;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 컨테이너 리포지토리 커스텀 인터페이스
 * QueryDSL을 사용한 복잡한 쿼리를 위한 인터페이스
 */
public interface ContainerRepositoryCustom {
    
    /**
     * 사용자가 특정 권한으로 접근 가능한 컨테이너 목록 조회
     * @param member 사용자
     * @param authority 최소 필요 권한
     * @return 접근 가능한 컨테이너 목록
     */
    List<Container> findAccessibleContainersByAuthority(Member member, Authority authority);
    
    /**
     * 컨테이너와 연관된 모든 데이터를 한번에 조회 (Fetch Join)
     * @param containerId 컨테이너 ID
     * @return 컨테이너 정보 (팀, 멤버 정보 포함)
     */
    Optional<Container> findContainerWithDetails(Long containerId);
    
    /**
     * 특정 기간 동안 활동하지 않은 멤버가 있는 컨테이너 목록 조회
     * @param inactiveDate 비활동 기준 날짜
     * @return 비활동 멤버가 있는 컨테이너 목록
     */
    List<Container> findContainersWithInactiveMembers(LocalDateTime inactiveDate);
    
    /**
     * 동적 검색 조건으로 컨테이너 검색
     * @param name 컨테이너 이름 (부분 일치)
     * @param visibility 공개 여부
     * @param ownerId 소유자 ID
     * @param memberId 참여 멤버 ID
     * @return 검색 결과
     */
    List<Container> searchContainers(String name, ContainerVisibility visibility, 
                                   String ownerId, String memberId);
    
    /**
     * 사용자의 권한별 컨테이너 개수 조회
     * @param member 사용자
     * @return 권한별 컨테이너 개수 (예: ROOT-2개, WRITE-3개)
     */
    List<ContainerAuthorityCount> countContainersByAuthority(Member member);
    
    /**
     * 컨테이너의 멤버 수와 최근 활동 시간 조회
     * @param containerId 컨테이너 ID
     * @return 컨테이너 통계 정보
     */
    ContainerStatistics getContainerStatistics(Long containerId);
    
    /**
     * 권한별 컨테이너 개수를 담는 DTO
     */
    class ContainerAuthorityCount {
        private Authority authority;
        private Long count;
        
        public ContainerAuthorityCount(Authority authority, Long count) {
            this.authority = authority;
            this.count = count;
        }
        
        // getter/setter
        public Authority getAuthority() { return authority; }
        public Long getCount() { return count; }
    }
    
    /**
     * 컨테이너 통계 정보 DTO
     */
    class ContainerStatistics {
        private Long memberCount;
        private LocalDateTime lastActivityDate;
        private Long activeMemberCount; // 최근 30일 내 활동한 멤버 수
        
        public ContainerStatistics(Long memberCount, LocalDateTime lastActivityDate, Long activeMemberCount) {
            this.memberCount = memberCount;
            this.lastActivityDate = lastActivityDate;
            this.activeMemberCount = activeMemberCount;
        }
        
        // getter/setter
        public Long getMemberCount() { return memberCount; }
        public LocalDateTime getLastActivityDate() { return lastActivityDate; }
        public Long getActiveMemberCount() { return activeMemberCount; }
    }
}