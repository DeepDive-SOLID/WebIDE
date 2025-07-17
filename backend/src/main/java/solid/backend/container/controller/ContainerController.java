package solid.backend.container.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import solid.backend.container.dto.*;
import solid.backend.container.service.ContainerService;
import solid.backend.common.enums.Authority;
import solid.backend.common.enums.ContainerVisibility;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;

/**
 * 컨테이너 관리 REST API 컨트롤러
 * 컨테이너 CRUD, 멤버 관리, 권한 관리 등의 API 엔드포인트 제공
 */
@RestController
@RequestMapping("/api/containers")
@RequiredArgsConstructor
public class ContainerController {
    
    private final ContainerService containerService;
    
    /**
     * 현재 인증된 사용자의 ID를 가져오는 헬퍼 메서드
     */
    private String getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
    
    /**
     * 컨테이너 생성
     * @param createDto 컨테이너 생성 정보 - 컨테이너 이름, 내용, 공개여부, 초대할 멤버 목록 포함
     * @return 생성된 컨테이너 정보 - 컨테이너 ID, 이름, 소유자 정보, 권한, 멤버 수 포함
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
     * @param containerId 컨테이너 ID - 조회할 컨테이너의 고유 식별자
     * @return 컨테이너 상세 정보 - 컨테이너 정보와 요청자의 권한 정보 포함
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
     * @return 소유한 컨테이너 목록 - 사용자가 소유자(ROOT)인 모든 컨테이너 목록
     */
    @GetMapping("/my")
    public ResponseEntity<List<ContainerResponseDto>> getMyContainers() {
        String memberId = getCurrentMemberId();
        List<ContainerResponseDto> response = containerService.getMyContainers(memberId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 공유된 컨테이너 목록 조회
     * @return 참여중인 컨테이너 목록 (소유 컨테이너 제외) - 멤버로 초대받아 참여중인 컨테이너만 반환
     */
    @GetMapping("/shared")
    public ResponseEntity<List<ContainerResponseDto>> getSharedContainers() {
        String memberId = getCurrentMemberId();
        List<ContainerResponseDto> response = containerService.getSharedContainers(memberId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 공개 컨테이너 목록 조회
     * @return 모든 PUBLIC 컨테이너 목록 - 인증된 사용자의 경우 참여 여부 및 권한 정보 포함
     */
    @GetMapping("/public")
    public ResponseEntity<List<ContainerResponseDto>> getPublicContainers() {
        String memberId = getCurrentMemberId(); // nullable
        List<ContainerResponseDto> response = containerService.getPublicContainers(memberId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 접근 가능한 모든 컨테이너 목록 조회
     * @return 소유 + 참여중인 모든 컨테이너 목록 - 사용자가 접근 가능한 모든 컨테이너와 각각의 권한 정보
     */
    @GetMapping
    public ResponseEntity<List<ContainerResponseDto>> getAllAccessibleContainers() {
        String memberId = getCurrentMemberId();
        List<ContainerResponseDto> response = containerService.getAllAccessibleContainers(memberId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 컨테이너 정보 수정
     * @param containerId 컨테이너 ID - 수정할 컨테이너의 고유 식별자
     * @param updateDto 수정할 정보 - 컨테이너 이름, 내용, 공개여부 중 수정할 항목만 포함
     * @return 수정된 컨테이너 정보 - 업데이트된 컨테이너 정보와 권한 정보
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
     * @param containerId 컨테이너 ID - 삭제할 컨테이너의 고유 식별자
     * @return 204 No Content - 성공 시 응답 본문 없음
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
     * @param containerId 컨테이너 ID - 멤버를 초대할 컨테이너의 고유 식별자
     * @param inviteDto 초대 정보 - 초대할 멤버 ID와 부여할 권한(ROOT/USER) 포함
     * @return 초대된 멤버 정보 - 멤버 정보와 부여된 권한, 가입 일시 포함
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
     * @param containerId 컨테이너 ID - 멤버 목록을 조회할 컨테이너의 고유 식별자
     * @return 컨테이너 멤버 목록 - 각 멤버의 정보, 권한, 가입일, 최근 활동일 포함
     */
    @GetMapping("/{containerId}/members")
    public ResponseEntity<List<GroupMemberResponseDto>> getContainerMembers(
            @PathVariable Long containerId) {
        String memberId = getCurrentMemberId();
        List<GroupMemberResponseDto> response = containerService.getContainerMembers(containerId, memberId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 멤버 권한 변경 (ROOT 권한 필요)
     * @param containerId 컨테이너 ID - 권한을 변경할 컨테이너의 고유 식별자
     * @param targetMemberId 대상 멤버 ID - 권한을 변경할 멤버의 고유 식별자
     * @param newAuthority 변경할 권한 - ROOT 또는 USER 중 선택
     * @return 204 No Content - 성공 시 응답 본문 없음
     */
    @PutMapping("/{containerId}/members/{targetMemberId}/authority")
    public ResponseEntity<Void> updateMemberAuthority(
            @PathVariable Long containerId,
            @PathVariable String targetMemberId,
            @RequestParam Authority newAuthority) {
        String requesterId = getCurrentMemberId();
        containerService.updateMemberAuthority(containerId, requesterId, targetMemberId, newAuthority);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 멤버 제거 (ROOT 권한 필요)
     * @param containerId 컨테이너 ID - 멤버를 제거할 컨테이너의 고유 식별자
     * @param targetMemberId 제거할 멤버 ID - 컨테이너에서 제거할 멤버의 고유 식별자
     * @return 204 No Content - 성공 시 응답 본문 없음
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
     * @param containerId 컨테이너 ID - 탈퇴할 컨테이너의 고유 식별자
     * @return 204 No Content - 성공 시 응답 본문 없음
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
     * @param containerId 컨테이너 ID - 활동을 기록할 컨테이너의 고유 식별자
     * @return 204 No Content - 성공 시 응답 본문 없음, 6개월 비활동 자동 탈퇴 방지용
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
     * @param name 컨테이너 이름 (선택) - 부분 일치 검색, 대소문자 구분 없음
     * @param visibility 공개 여부 (선택) - PUBLIC 또는 PRIVATE
     * @param ownerId 소유자 ID (선택) - 특정 사용자가 소유한 컨테이너만 필터링
     * @return 검색 결과 - 조건에 맞는 컨테이너 목록과 각 컨테이너에 대한 요청자의 권한
     */
    @GetMapping("/search")
    public ResponseEntity<List<ContainerResponseDto>> searchContainers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ContainerVisibility visibility,
            @RequestParam(required = false) String ownerId) {
        String memberId = getCurrentMemberId();
        List<ContainerResponseDto> response = containerService.searchContainers(name, visibility, ownerId, memberId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 사용자의 권한별 컨테이너 통계
     * @return 권한별 컨테이너 개수 - ROOT, USER 권한별로 참여중인 컨테이너 수
     */
    @GetMapping("/stats/authority")
    public ResponseEntity<Map<Authority, Long>> getContainerStatsByAuthority() {
        String memberId = getCurrentMemberId();
        Map<Authority, Long> response = containerService.getContainerStatsByAuthority(memberId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 컨테이너 상세 통계 정보
     * @param containerId 컨테이너 ID - 통계를 조회할 컨테이너의 고유 식별자  
     * @return 컨테이너 통계 정보 - 전체 멤버 수, 활동/비활동 멤버 수, 권한별 분포, 활동률 등
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
}