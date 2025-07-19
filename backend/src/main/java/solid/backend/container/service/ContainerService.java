package solid.backend.container.service;

import solid.backend.container.dto.*;

import java.util.List;
import java.util.Map;

/**
 * 컨테이너 서비스 인터페이스
 */
public interface ContainerService {
    
    /**
     * 컨테이너 생성
     * @param memberId 생성자 ID
     * @param createDto 컨테이너 생성 정보
     * @return 생성된 컨테이너 정보
     * @throws MemberNotFoundException 회원을 찾을 수 없는 경우
     * @throws InvalidMemberException 유효하지 않은 멤버 ID가 포함된 경우
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
     * 소유한 컨테이너 목록 조회
     * @param memberId 사용자 ID
     * @return 소유 컨테이너 목록
     */
    List<ContainerResponseDto> getMyContainers(String memberId);
    
    /**
     * 공유된 컨테이너 목록 조회
     * @param memberId 사용자 ID
     * @return 공유된 컨테이너 목록
     */
    List<ContainerResponseDto> getSharedContainers(String memberId);
    
    /**
     * 공개 컨테이너 목록 조회
     * @param memberId 사용자 ID (null 가능)
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
     * 컨테이너 삭제
     * @param containerId 컨테이너 ID
     * @param memberId 삭제하는 사용자 ID
     */
    void deleteContainer(Long containerId, String memberId);
    
    /**
     * 컨테이너에 멤버 초대
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
     * @throws ContainerNotFoundException 컨테이너를 찾을 수 없는 경우
     * @throws IllegalArgumentException 접근 권한이 없는 경우
     */
    List<GroupMemberResponseDto> getContainerMembers(Long containerId, String memberId);
    
    /**
     * 멤버 제거
     * @param containerId 컨테이너 ID
     * @param requesterId 요청하는 사용자 ID
     * @param targetMemberId 제거할 멤버 ID
     */
    void removeMember(Long containerId, String requesterId, String targetMemberId);
    
    /**
     * 컨테이너 탈퇴
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
     * 미활동 멤버 자동 제거
     */
    void removeInactiveMembers();
    
    /**
     * 컨테이너 검색
     * @param name 컨테이너 이름
     * @param isPublic 공개 여부
     * @param ownerId 소유자 ID
     * @param memberId 참여 멤버 ID
     * @return 검색 결과
     */
    List<ContainerResponseDto> searchContainers(String name, Boolean isPublic, 
                                               String ownerId, String memberId);
    
    /**
     * 권한별 컨테이너 통계 조회
     * @param memberId 사용자 ID
     * @return 권한별 컨테이너 개수
     */
    Map<String, Long> getContainerStatsByAuthority(String memberId);
    
    /**
     * 컨테이너 통계 정보 조회
     * @param containerId 컨테이너 ID
     * @return 컨테이너 통계 정보
     */
    ContainerStatisticsDto getContainerStatistics(Long containerId);
    
    /**
     * 여러 컨테이너의 공개 상태 변경
     * @param containerIds 변경할 컨테이너 ID 목록
     * @param isPublic 변경할 공개 상태
     * @param requesterId 요청자 ID
     * @return 업데이트된 컨테이너 수
     */
    long batchUpdateVisibility(List<Long> containerIds, Boolean isPublic, String requesterId);
    
    /**
     * 컨테이너 고급 검색
     * @param searchDto 검색 조건
     * @param memberId 조회하는 사용자 ID
     * @return 검색 결과
     */
    List<ContainerResponseDto> advancedSearch(ContainerSearchDto searchDto, String memberId);
}