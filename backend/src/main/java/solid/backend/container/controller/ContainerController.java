package solid.backend.container.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import solid.backend.container.dto.*;
import solid.backend.container.service.ContainerService;
import solid.backend.common.enums.Authority;

import java.util.List;

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
     * 컨테이너 생성
     * @param memberId 생성자 ID (헤더)
     * @param createDto 컨테이너 생성 정보
     * @return 생성된 컨테이너 정보
     */
    @PostMapping
    public ResponseEntity<ContainerResponseDto> createContainer(
            @RequestHeader("memberId") String memberId,
            @RequestBody @Valid ContainerCreateDto createDto) {
        ContainerResponseDto response = containerService.createContainer(memberId, createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 컨테이너 상세 조회
     * @param containerId 컨테이너 ID
     * @param memberId 조회하는 사용자 ID (헤더)
     * @return 컨테이너 상세 정보
     */
    @GetMapping("/{containerId}")
    public ResponseEntity<ContainerResponseDto> getContainer(
            @PathVariable Long containerId,
            @RequestHeader("memberId") String memberId) {
        ContainerResponseDto response = containerService.getContainer(containerId, memberId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 내 컨테이너 목록 조회
     * @param memberId 사용자 ID (헤더)
     * @return 소유한 컨테이너 목록
     */
    @GetMapping("/my")
    public ResponseEntity<List<ContainerResponseDto>> getMyContainers(
            @RequestHeader("memberId") String memberId) {
        List<ContainerResponseDto> response = containerService.getMyContainers(memberId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 공유된 컨테이너 목록 조회
     * @param memberId 사용자 ID (헤더)
     * @return 참여중인 컨테이너 목록 (소유 컨테이너 제외)
     */
    @GetMapping("/shared")
    public ResponseEntity<List<ContainerResponseDto>> getSharedContainers(
            @RequestHeader("memberId") String memberId) {
        List<ContainerResponseDto> response = containerService.getSharedContainers(memberId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 공개 컨테이너 목록 조회
     * @return 모든 PUBLIC 컨테이너 목록
     */
    @GetMapping("/public")
    public ResponseEntity<List<ContainerResponseDto>> getPublicContainers() {
        List<ContainerResponseDto> response = containerService.getPublicContainers();
        return ResponseEntity.ok(response);
    }
    
    /**
     * 접근 가능한 모든 컨테이너 목록 조회
     * @param memberId 사용자 ID (헤더)
     * @return 소유 + 참여중인 모든 컨테이너 목록
     */
    @GetMapping
    public ResponseEntity<List<ContainerResponseDto>> getAllAccessibleContainers(
            @RequestHeader("memberId") String memberId) {
        List<ContainerResponseDto> response = containerService.getAllAccessibleContainers(memberId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 컨테이너 정보 수정 (소유자만 가능)
     * @param containerId 컨테이너 ID
     * @param memberId 수정하는 사용자 ID (헤더)
     * @param updateDto 수정할 정보
     * @return 수정된 컨테이너 정보
     */
    @PutMapping("/{containerId}")
    public ResponseEntity<ContainerResponseDto> updateContainer(
            @PathVariable Long containerId,
            @RequestHeader("memberId") String memberId,
            @RequestBody @Valid ContainerUpdateDto updateDto) {
        ContainerResponseDto response = containerService.updateContainer(containerId, memberId, updateDto);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 컨테이너 삭제 (소유자만 가능)
     * @param containerId 컨테이너 ID
     * @param memberId 삭제하는 사용자 ID (헤더)
     * @return 204 No Content
     */
    @DeleteMapping("/{containerId}")
    public ResponseEntity<Void> deleteContainer(
            @PathVariable Long containerId,
            @RequestHeader("memberId") String memberId) {
        containerService.deleteContainer(containerId, memberId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 컨테이너에 멤버 초대 (ROOT 권한 필요)
     * @param containerId 컨테이너 ID
     * @param requesterId 초대하는 사용자 ID (헤더)
     * @param inviteDto 초대 정보 (대상 ID, 부여 권한)
     * @return 초대된 멤버 정보
     */
    @PostMapping("/{containerId}/members")
    public ResponseEntity<GroupMemberResponseDto> inviteMember(
            @PathVariable Long containerId,
            @RequestHeader("memberId") String requesterId,
            @RequestBody @Valid MemberInviteDto inviteDto) {
        GroupMemberResponseDto response = containerService.inviteMember(containerId, requesterId, inviteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 컨테이너 멤버 목록 조회
     * @param containerId 컨테이너 ID
     * @param memberId 조회하는 사용자 ID (헤더)
     * @return 컨테이너 멤버 목록
     */
    @GetMapping("/{containerId}/members")
    public ResponseEntity<List<GroupMemberResponseDto>> getContainerMembers(
            @PathVariable Long containerId,
            @RequestHeader("memberId") String memberId) {
        List<GroupMemberResponseDto> response = containerService.getContainerMembers(containerId, memberId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 멤버 권한 변경 (ROOT 권한 필요)
     * @param containerId 컨테이너 ID
     * @param targetMemberId 대상 멤버 ID
     * @param requesterId 요청자 ID (헤더)
     * @param newAuthority 변경할 권한
     * @return 204 No Content
     */
    @PutMapping("/{containerId}/members/{targetMemberId}/authority")
    public ResponseEntity<Void> updateMemberAuthority(
            @PathVariable Long containerId,
            @PathVariable String targetMemberId,
            @RequestHeader("memberId") String requesterId,
            @RequestParam Authority newAuthority) {
        containerService.updateMemberAuthority(containerId, requesterId, targetMemberId, newAuthority);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 멤버 제거 (ROOT 권한 필요)
     * @param containerId 컨테이너 ID
     * @param targetMemberId 제거할 멤버 ID
     * @param requesterId 요청자 ID (헤더)
     * @return 204 No Content
     */
    @DeleteMapping("/{containerId}/members/{targetMemberId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long containerId,
            @PathVariable String targetMemberId,
            @RequestHeader("memberId") String requesterId) {
        containerService.removeMember(containerId, requesterId, targetMemberId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 컨테이너 탈퇴 (소유자는 불가)
     * @param containerId 컨테이너 ID
     * @param memberId 탈퇴하는 사용자 ID (헤더)
     * @return 204 No Content
     */
    @DeleteMapping("/{containerId}/members/me")
    public ResponseEntity<Void> leaveContainer(
            @PathVariable Long containerId,
            @RequestHeader("memberId") String memberId) {
        containerService.leaveContainer(containerId, memberId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 멤버 활동 시간 업데이트
     * @param containerId 컨테이너 ID
     * @param memberId 사용자 ID (헤더)
     * @return 204 No Content
     */
    @PutMapping("/{containerId}/members/me/activity")
    public ResponseEntity<Void> updateActivity(
            @PathVariable Long containerId,
            @RequestHeader("memberId") String memberId) {
        containerService.updateMemberActivity(containerId, memberId);
        return ResponseEntity.noContent().build();
    }
}