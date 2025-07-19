package solid.backend.container.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import solid.backend.container.dto.*;
import solid.backend.container.service.ContainerService;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import solid.backend.common.ApiResponse;

/**
 * 컨테이너 관리 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/containers")
@RequiredArgsConstructor
public class ContainerController {
    
    /** 컨테이너 비즈니스 로직 처리 서비스 */
    private final ContainerService containerService;
    
    /**
     * 현재 인증된 사용자의 ID를 가져오는 헬퍼 메서드
     * @return 사용자 ID
     */
    private String getCurrentMemberId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
    
    /**
     * 컨테이너 생성
     * @param createDto 컨테이너 생성 정보
     * @return 생성된 컨테이너 정보
     */
    @PostMapping
    public ResponseEntity<ContainerResponseDto> createContainer(
            @RequestBody @Valid ContainerCreateDto createDto) {
        String memberId = getCurrentMemberId();
        ContainerResponseDto response = containerService.createContainer(memberId, createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 컨테이너 상세 조회
     * @param containerId 컨테이너 ID
     * @return 컨테이너 상세 정보
     */
    @GetMapping("/{containerId}")
    public ResponseEntity<ContainerResponseDto> getContainer(
            @PathVariable Long containerId) {
        String memberId = getCurrentMemberId();
        ContainerResponseDto response = containerService.getContainer(containerId, memberId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 내 컨테이너 목록 조회
     * @return 소유한 컨테이너 목록
     */
    @GetMapping("/my")
    public ResponseEntity<List<ContainerResponseDto>> getMyContainers() {
        String memberId = getCurrentMemberId();
        List<ContainerResponseDto> response = containerService.getMyContainers(memberId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 공유된 컨테이너 목록 조회
     * @return 참여중인 컨테이너 목록 (소유 컨테이너 제외)
     */
    @GetMapping("/shared")
    public ResponseEntity<List<ContainerResponseDto>> getSharedContainers() {
        String memberId = getCurrentMemberId();
        List<ContainerResponseDto> response = containerService.getSharedContainers(memberId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 공개 컨테이너 목록 조회
     * @return 모든 PUBLIC 컨테이너 목록
     */
    @GetMapping("/public")
    public ResponseEntity<List<ContainerResponseDto>> getPublicContainers() {
        String memberId = getCurrentMemberId(); // nullable
        List<ContainerResponseDto> response = containerService.getPublicContainers(memberId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 접근 가능한 모든 컨테이너 목록 조회
     * @return 소유 + 참여중인 모든 컨테이너 목록
     */
    @GetMapping
    public ResponseEntity<List<ContainerResponseDto>> getAllAccessibleContainers() {
        String memberId = getCurrentMemberId();
        List<ContainerResponseDto> response = containerService.getAllAccessibleContainers(memberId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 컨테이너 정보 수정
     * @param containerId 컨테이너 ID
     * @param updateDto 수정할 정보
     * @return 수정된 컨테이너 정보
     */
    @PutMapping("/{containerId}")
    public ResponseEntity<ContainerResponseDto> updateContainer(
            @PathVariable Long containerId,
            @RequestBody @Valid ContainerUpdateDto updateDto) {
        String memberId = getCurrentMemberId();
        ContainerResponseDto response = containerService.updateContainer(containerId, memberId, updateDto);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 컨테이너 삭제 (ROOT 권한 필요)
     * @param containerId 컨테이너 ID
     * @return 204 No Content
     */
    @DeleteMapping("/{containerId}")
    public ResponseEntity<Void> deleteContainer(
            @PathVariable Long containerId) {
        String memberId = getCurrentMemberId();
        containerService.deleteContainer(containerId, memberId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 컨테이너에 멤버 초대 (ROOT 권한 필요)
     * @param containerId 컨테이너 ID
     * @param inviteDto 초대 정보
     * @return 초대된 멤버 정보
     */
    @PostMapping("/{containerId}/members")
    public ResponseEntity<GroupMemberResponseDto> inviteMember(
            @PathVariable Long containerId,
            @RequestBody @Valid MemberInviteDto inviteDto) {
        String requesterId = getCurrentMemberId();
        GroupMemberResponseDto response = containerService.inviteMember(containerId, requesterId, inviteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 컨테이너 멤버 목록 조회
     * @param containerId 컨테이너 ID
     * @return 컨테이너 멤버 목록
     */
    @GetMapping("/{containerId}/members")
    public ResponseEntity<List<GroupMemberResponseDto>> getContainerMembers(
            @PathVariable Long containerId) {
        String memberId = getCurrentMemberId();
        List<GroupMemberResponseDto> response = containerService.getContainerMembers(containerId, memberId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 멤버 제거 (ROOT 권한 필요)
     * @param containerId 컨테이너 ID
     * @param targetMemberId 제거할 멤버 ID
     * @return 204 No Content
     */
    @DeleteMapping("/{containerId}/members/{targetMemberId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long containerId,
            @PathVariable String targetMemberId) {
        String requesterId = getCurrentMemberId();
        containerService.removeMember(containerId, requesterId, targetMemberId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 컨테이너 탈퇴 (소유자는 불가)
     * @param containerId 컨테이너 ID
     * @return 204 No Content
     */
    @DeleteMapping("/{containerId}/members/me")
    public ResponseEntity<Void> leaveContainer(
            @PathVariable Long containerId) {
        String memberId = getCurrentMemberId();
        containerService.leaveContainer(containerId, memberId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 멤버 활동 시간 업데이트
     * @param containerId 컨테이너 ID
     * @return 204 No Content
     */
    @PutMapping("/{containerId}/members/me/activity")
    public ResponseEntity<Void> updateActivity(
            @PathVariable Long containerId) {
        String memberId = getCurrentMemberId();
        containerService.updateMemberActivity(containerId, memberId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 컨테이너 검색 (QueryDSL 활용)
     * @param name 컨테이너 이름 (선택)
     * @param isPublic 공개 여부 (선택)
     * @param ownerId 소유자 ID (선택)
     * @return 검색 결과
     */
    @GetMapping("/search")
    public ResponseEntity<List<ContainerResponseDto>> searchContainers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isPublic,
            @RequestParam(required = false) String ownerId) {
        String memberId = getCurrentMemberId();
        List<ContainerResponseDto> response = containerService.searchContainers(name, isPublic, ownerId, memberId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 컨테이너 고급 검색
     * @param searchDto 검색 조건
     * @return 검색 결과
     */
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<ContainerResponseDto>>> advancedSearch(
            @RequestBody @Valid ContainerSearchDto searchDto) {
        String memberId = getCurrentMemberId();
        List<ContainerResponseDto> result = containerService.advancedSearch(searchDto, memberId);
        return ResponseEntity.ok(ApiResponse.success(
            result,
            "검색이 완료되었습니다"
        ));
    }
    
    /**
     * 사용자의 권한별 컨테이너 통계
     * @return 권한별 컨테이너 개수
     */
    @GetMapping("/stats/authority")
    public ResponseEntity<Map<String, Long>> getContainerStatsByAuthority() {
        String memberId = getCurrentMemberId();
        Map<String, Long> response = containerService.getContainerStatsByAuthority(memberId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 컨테이너 상세 통계 정보
     * @param containerId 컨테이너 ID
     * @return 컨테이너 통계 정보
     */
    @GetMapping("/{containerId}/statistics")
    public ResponseEntity<ContainerStatisticsDto> getContainerStatistics(
            @PathVariable Long containerId) {
        String memberId = getCurrentMemberId();
        // 접근 권한 확인
        containerService.getContainer(containerId, memberId);
        ContainerStatisticsDto response = containerService.getContainerStatistics(containerId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 여러 컨테이너의 공개 상태 변경
     * @param batchDto 배치 작업 정보
     * @return 업데이트 결과
     */
    @PutMapping("/batch/visibility")
    public ResponseEntity<Map<String, Object>> batchUpdateVisibility(
            @RequestBody @Valid BatchContainerVisibilityDto batchDto) {
        String memberId = getCurrentMemberId();
        long updatedCount = containerService.batchUpdateVisibility(
            batchDto.getContainerIds(), 
            batchDto.getIsPublic(), 
            memberId
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("updatedCount", updatedCount);
        response.put("requestedCount", batchDto.getContainerIds().size());
        
        return ResponseEntity.ok(response);
    }

}