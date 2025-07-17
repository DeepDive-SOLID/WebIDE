package solid.backend.container.service;

import solid.backend.container.dto.*;
import solid.backend.common.enums.Authority;
import solid.backend.common.enums.ContainerVisibility;

import java.util.List;
import java.util.Map;

/**
 * 컨테이너 서비스 인터페이스
 * 컨테이너 관련 비즈니스 로직을 정의
 */
public interface ContainerService {
    
    /**
     * 컨테이너 생성
     * @param memberId 생성자 ID
     * @param createDto 컨테이너 생성 정보
     * @return 생성된 컨테이너 정보
     */
    ContainerResponseDto createContainer(String memberId, ContainerCreateDto createDto);
    
    /**
     * 컨테이너 조회
     * @param containerId 컨테이너 ID
     * @param memberId 조회하는 사용자 ID
     * @return 컨테이너 정보
     */
    ContainerResponseDto getContainer(Long containerId, String memberId);
    
    /**
     * 내가 소유한 컨테이너 목록 조회
     * @param memberId 사용자 ID
     * @return 소유 컨테이너 목록
     */
    List<ContainerResponseDto> getMyContainers(String memberId);
    
    /**
     * 공유된 컨테이너 목록 조회 (타인이 소유하고 내가 참여한 컨테이너)
     * @param memberId 사용자 ID
     * @return 공유된 컨테이너 목록
     */
    List<ContainerResponseDto> getSharedContainers(String memberId);
    
    /**
     * 공개 컨테이너 목록 조회
     * @param memberId 사용자 ID (null 가능) - 인증된 사용자의 경우 참여 여부 표시용
     * @return 공개 컨테이너 목록
     */
    List<ContainerResponseDto> getPublicContainers(String memberId);
    
    /**
     * 접근 가능한 모든 컨테이너 목록 조회
     * @param memberId 사용자 ID
     * @return 접근 가능한 모든 컨테이너 목록
     */
    List<ContainerResponseDto> getAllAccessibleContainers(String memberId);
    
    /**
     * 컨테이너 정보 수정
     * @param containerId 컨테이너 ID
     * @param memberId 수정하는 사용자 ID
     * @param updateDto 수정할 정보
     * @return 수정된 컨테이너 정보
     */
    ContainerResponseDto updateContainer(Long containerId, String memberId, ContainerUpdateDto updateDto);
    
    /**
     * 컨테이너 삭제 (ROOT 권한 필요)
     * @param containerId 컨테이너 ID
     * @param memberId 삭제하는 사용자 ID
     */
    void deleteContainer(Long containerId, String memberId);
    
    /**
     * 컨테이너에 멤버 초대 (ROOT 권한 필요)
     * @param containerId 컨테이너 ID
     * @param requesterId 초대하는 사용자 ID
     * @param inviteDto 초대 정보
     * @return 초대된 멤버 정보
     */
    GroupMemberResponseDto inviteMember(Long containerId, String requesterId, MemberInviteDto inviteDto);
    
    /**
     * 컨테이너 멤버 목록 조회
     * @param containerId 컨테이너 ID
     * @param memberId 조회하는 사용자 ID
     * @return 멤버 목록
     */
    List<GroupMemberResponseDto> getContainerMembers(Long containerId, String memberId);
    
    /**
     * 멤버 권한 변경 (ROOT 권한 필요)
     * @param containerId 컨테이너 ID
     * @param requesterId 요청하는 사용자 ID
     * @param targetMemberId 대상 멤버 ID
     * @param newAuthority 새로운 권한
     */
    void updateMemberAuthority(Long containerId, String requesterId, String targetMemberId, Authority newAuthority);
    
    /**
     * 멤버 제거 (ROOT 권한 필요)
     * @param containerId 컨테이너 ID
     * @param requesterId 요청하는 사용자 ID
     * @param targetMemberId 제거할 멤버 ID
     */
    void removeMember(Long containerId, String requesterId, String targetMemberId);
    
    /**
     * 컨테이너 탈퇴 (소유자는 불가)
     * @param containerId 컨테이너 ID
     * @param memberId 탈퇴하는 사용자 ID
     */
    void leaveContainer(Long containerId, String memberId);
    
    /**
     * 멤버 활동 시간 업데이트
     * @param containerId 컨테이너 ID
     * @param memberId 사용자 ID
     */
    void updateMemberActivity(Long containerId, String memberId);
    
    /**
     * 6개월 이상 미활동 멤버 자동 제거 (스케줄러에서 호출)
     */
    void removeInactiveMembers();
    
    /**
     * 컨테이너 검색 (QueryDSL 활용)
     * @param name 컨테이너 이름 (부분 검색)
     * @param visibility 공개 여부
     * @param ownerId 소유자 ID
     * @param memberId 참여 멤버 ID
     * @return 검색 결과
     */
    List<ContainerResponseDto> searchContainers(String name, ContainerVisibility visibility, 
                                               String ownerId, String memberId);
    
    /**
     * 사용자의 권한별 컨테이너 통계
     * @param memberId 사용자 ID
     * @return 권한별 컨테이너 개수
     */
    Map<Authority, Long> getContainerStatsByAuthority(String memberId);
    
    /**
     * 컨테이너 상세 통계 정보
     * @param containerId 컨테이너 ID
     * @return 멤버 수, 활동 멤버 수, 최근 활동 시간 등
     */
    ContainerStatisticsDto getContainerStatistics(Long containerId);
}