package solid.backend.container.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solid.backend.container.dto.*;
import solid.backend.entity.*;
import solid.backend.common.enums.Authority;
import solid.backend.common.enums.ContainerVisibility;
import solid.backend.jpaRepository.ContainerRepository;
import solid.backend.jpaRepository.MemberRepository;
import solid.backend.jpaRepository.AuthRepository;
import solid.backend.container.exception.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import solid.backend.jpaRepository.ContainerRepositoryCustom.ContainerAuthorityCount;
import solid.backend.jpaRepository.ContainerRepositoryCustom.ContainerStatistics;

/**
 * 컨테이너 서비스 구현체
 * 컨테이너 관련 모든 비즈니스 로직 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContainerServiceImpl implements ContainerService {
    
    private final ContainerRepository containerRepository;
    private final MemberRepository memberRepository;
    private final AuthRepository authRepository;
    
    /**
     * 컨테이너 생성
     * 
     * @param memberId 컨테이너를 생성하는 사용자 ID (소유자가 됨)
     * @param createDto 컨테이너 생성 정보 (containerName, containerContent, visibility, invitedMemberIds)
     * @return 생성된 컨테이너 정보
     * @throws MemberNotFoundException 존재하지 않는 회원 ID인 경우
     * @throws InvalidMemberException 초대할 회원이 존재하지 않는 경우
     */
    @Override
    @Transactional
    public ContainerResponseDto createContainer(String memberId, ContainerCreateDto createDto) {
        // 컨테이너 소유자 확인
        Member owner = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("회원을 찾을 수 없습니다: " + memberId));
        
        // 팀 생성
        Team team = new Team();
        team.setTeamName(createDto.getContainerName());
        
        // 컨테이너 생성
        Container container = Container.builder()
                .containerName(createDto.getContainerName())
                .containerContent(createDto.getContainerContent())
                .containerAuth(createDto.getVisibility() == ContainerVisibility.PUBLIC)
                .owner(owner)
                .team(team)
                .build();
        
        Container savedContainer = containerRepository.save(container);
        
        // 소유자를 팀에 ROOT 권한으로 추가
        Auth rootAuth = authRepository.findById("ROOT")
                .orElseGet(() -> {
                    Auth newAuth = new Auth();
                    newAuth.setAuthId("ROOT");
                    newAuth.setAuthName("관리자");
                    return authRepository.save(newAuth);
                });
        
        TeamUser ownerMember = new TeamUser();
        ownerMember.setTeam(team);
        ownerMember.setMember(owner);
        ownerMember.setTeamAuth(rootAuth);
        ownerMember.setJoinedDate(LocalDateTime.now());
        ownerMember.setLastActivityDate(LocalDateTime.now());
        
        team.getTeamUsers().add(ownerMember);
        
        // 초대 멤버 처리
        if (createDto.getInvitedMemberIds() != null && !createDto.getInvitedMemberIds().isEmpty()) {
            // 존재하지 않는 멤버 확인
            List<String> invalidMemberIds = new ArrayList<>();
            for (String invitedMemberId : createDto.getInvitedMemberIds()) {
                if (!memberRepository.existsById(invitedMemberId)) {
                    invalidMemberIds.add(invitedMemberId);
                }
            }
            
            if (!invalidMemberIds.isEmpty()) {
                throw new InvalidMemberException("존재하지 않는 멤버 ID: " + String.join(", ", invalidMemberIds));
            }
            
            // 기본값은 USER 권한으로 설정
            Auth userAuth = authRepository.findById("USER")
                    .orElseGet(() -> {
                        Auth newAuth = new Auth();
                        newAuth.setAuthId("USER");
                        newAuth.setAuthName("사용자");
                        return authRepository.save(newAuth);
                    });
            
            for (String invitedMemberId : createDto.getInvitedMemberIds()) {
                Member invitedMember = memberRepository.findById(invitedMemberId).get();
                TeamUser teamUser = new TeamUser();
                teamUser.setTeam(team);
                teamUser.setMember(invitedMember);
                teamUser.setTeamAuth(userAuth);
                teamUser.setJoinedDate(LocalDateTime.now());
                teamUser.setLastActivityDate(LocalDateTime.now());
                team.getTeamUsers().add(teamUser);
            }
        }
        
        return ContainerResponseDto.from(savedContainer, "ROOT", savedContainer.getTeam().getTeamUsers().size());
    }
    
    /**
     * 컨테이너 상세 조회
     * 
     * @param containerId 조회할 컨테이너 ID
     * @param memberId 조회하는 사용자 ID
     * @return 컨테이너 상세 정보와 사용자의 권한
     * @throws ContainerNotFoundException 컨테이너가 존재하지 않는 경우
     * @throws IllegalArgumentException 비공개 컨테이너에 접근 권한이 없는 경우
     */
    @Override
    public ContainerResponseDto getContainer(Long containerId, String memberId) {
        // QueryDSL을 사용하여 연관 데이터를 한번에 조회
        Container container = containerRepository.findContainerWithDetails(containerId)
                .orElseThrow(() -> new ContainerNotFoundException("컨테이너를 찾을 수 없습니다: " + containerId));
        
        // 접근 권한 확인
        if (container.getVisibility() == ContainerVisibility.PRIVATE) {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
            
            // 소유자이거나 팀 멤버인 경우만 접근 가능
            boolean hasAccess = container.getOwner().equals(member) ||
                    container.getTeam().getTeamUsers().stream()
                            .anyMatch(tu -> tu.getMember().equals(member));
            
            if (!hasAccess) {
                throw new IllegalArgumentException("접근 권한이 없습니다.");
            }
        }
        
        // 사용자 권한 조회
        String userAuthority = getUserAuthority(container, memberId);
        
        return ContainerResponseDto.from(container, userAuthority, container.getTeam().getTeamUsers().size());
    }
    
    /**
     * 컨테이너 정보 업데이트
     * 
     * @param containerId 업데이트할 컨테이너 ID
     * @param memberId 업데이트를 요청하는 사용자 ID
     * @param updateDto 업데이트할 정보 (containerName, containerContent, visibility)
     * @return 업데이트된 컨테이너 정보
     * @throws ContainerNotFoundException 컨테이너가 존재하지 않는 경우
     * @throws UnauthorizedContainerAccessException 수정 권한이 없거나 설정 변경 권한이 없는 경우
     */
    @Override
    @Transactional
    public ContainerResponseDto updateContainer(Long containerId, String memberId, ContainerUpdateDto updateDto) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new ContainerNotFoundException("컨테이너를 찾을 수 없습니다: " + containerId));
        
        // 수정 권한 확인
        TeamUser teamUser = container.getTeam().getTeamUsers().stream()
                .filter(tu -> tu.getMember().getMemberId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedContainerAccessException("컨테이너 멤버가 아닙니다."));
        
        Authority userAuth = Authority.valueOf(teamUser.getTeamAuth().getAuthId());
        if (!userAuth.canWrite()) {
            throw new UnauthorizedContainerAccessException("수정 권한이 없습니다.");
        }
        
        // 컨테이너 설정 변경은 ROOT만 가능
        if (updateDto.getVisibility() != null && !userAuth.canManage()) {
            throw new UnauthorizedContainerAccessException("컨테이너 설정은 관리자만 변경할 수 있습니다.");
        }
        
        // 업데이트
        if (updateDto.getContainerName() != null) {
            container.setContainerName(updateDto.getContainerName());
            container.getTeam().setTeamName(updateDto.getContainerName());
        }
        if (updateDto.getContainerContent() != null) {
            container.setContainerContent(updateDto.getContainerContent());
        }
        if (updateDto.getVisibility() != null) {
            container.setVisibility(updateDto.getVisibility());
        }
        
        return ContainerResponseDto.from(container, userAuth.getCode(), container.getTeam().getTeamUsers().size());
    }
    
    /**
     * 컨테이너 삭제
     * 
     * @param containerId 삭제할 컨테이너 ID
     * @param memberId 삭제를 요청하는 사용자 ID
     * @throws ContainerNotFoundException 컨테이너가 존재하지 않는 경우
     * @throws UnauthorizedContainerAccessException ROOT 권한이 없는 경우
     */
    @Override
    @Transactional
    public void deleteContainer(Long containerId, String memberId) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new ContainerNotFoundException("컨테이너를 찾을 수 없습니다: " + containerId));
        
        // ROOT 권한 확인
        TeamUser teamUser = container.getTeam().getTeamUsers().stream()
                .filter(tu -> tu.getMember().getMemberId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedContainerAccessException("컨테이너 멤버가 아닙니다."));
        
        Authority userAuth = Authority.valueOf(teamUser.getTeamAuth().getAuthId());
        if (!userAuth.canManage()) {
            throw new UnauthorizedContainerAccessException("컨테이너 삭제는 관리자만 가능합니다.");
        }
        
        containerRepository.delete(container);
    }
    
    /**
     * 사용자가 소유한 컨테이너 목록 조회
     * 
     * @param memberId 조회하는 사용자 ID
     * @return 사용자가 소유자(owner)인 모든 컨테이너 목록
     * @throws IllegalArgumentException 회원이 존재하지 않는 경우
     */
    @Override
    public List<ContainerResponseDto> getMyContainers(String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        
        return containerRepository.findByOwner(member).stream()
                .map(container -> ContainerResponseDto.from(container, "ROOT", container.getTeam().getTeamUsers().size()))
                .collect(Collectors.toList());
    }
    
    /**
     * 사용자가 참여중인 컨테이너 목록 조회 (소유 컨테이너 제외)
     * 
     * @param memberId 조회하는 사용자 ID
     * @return 사용자가 멤버로 참여중이지만 소유자가 아닌 컨테이너 목록
     * @throws IllegalArgumentException 회원이 존재하지 않는 경우
     */
    @Override
    public List<ContainerResponseDto> getSharedContainers(String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        
        return containerRepository.findSharedContainers(member).stream()
                .map(container -> {
                    String authority = getUserAuthority(container, memberId);
                    return ContainerResponseDto.from(container, authority, container.getTeam().getTeamUsers().size());
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 모든 공개 컨테이너 목록 조회
     * 
     * @param memberId 사용자 ID (null 가능) - 인증된 사용자의 경우 참여 여부 표시용
     * @return containerAuth가 true인 모든 컨테이너 목록
     */
    @Override
    public List<ContainerResponseDto> getPublicContainers(String memberId) {
        return containerRepository.findByContainerAuth(true).stream()
                .map(container -> {
                    String authority = null;
                    if (memberId != null) {
                        authority = getUserAuthority(container, memberId);
                    }
                    return ContainerResponseDto.from(container, authority, container.getTeam().getTeamUsers().size());
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 사용자가 접근 가능한 모든 컨테이너 목록 조회
     * 
     * @param memberId 조회하는 사용자 ID
     * @return 소유한 컨테이너 + 참여중인 컨테이너 목록
     * @throws IllegalArgumentException 회원이 존재하지 않는 경우
     */
    @Override
    public List<ContainerResponseDto> getAllAccessibleContainers(String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        
        return containerRepository.findAllAccessibleContainers(member).stream()
                .map(container -> {
                    String authority = getUserAuthority(container, memberId);
                    return ContainerResponseDto.from(container, authority, container.getTeam().getTeamUsers().size());
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 컨테이너에 멤버 초대
     * 
     * @param containerId 멤버를 초대할 컨테이너 ID
     * @param requesterId 초대를 요청하는 사용자 ID
     * @param inviteDto 초대할 멤버 정보 (memberId, authority)
     * @return 초대된 멤버 정보
     * @throws ContainerNotFoundException 컨테이너가 어재하지 않는 경우
     * @throws UnauthorizedContainerAccessException ROOT 권한이 없는 경우
     * @throws MemberNotFoundException 초대할 회원이 존재하지 않는 경우
     * @throws DuplicateMemberException 이미 컨테이너 멤버인 경우
     */
    @Override
    @Transactional
    public GroupMemberResponseDto inviteMember(Long containerId, String requesterId, MemberInviteDto inviteDto) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new ContainerNotFoundException("컨테이너를 찾을 수 없습니다: " + containerId));
        
        // ROOT 권한 확인
        if (!hasRootAuthority(container, requesterId)) {
            throw new UnauthorizedContainerAccessException("ROOT 권한이 필요합니다.");
        }
        
        Member invitedMember = memberRepository.findById(inviteDto.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException("초대할 회원을 찾을 수 없습니다: " + inviteDto.getMemberId()));
        
        // 이미 멤버인지 확인
        boolean alreadyMember = container.getTeam().getTeamUsers().stream()
                .anyMatch(tu -> tu.getMember().equals(invitedMember));
        
        if (alreadyMember) {
            throw new DuplicateMemberException("이미 멤버입니다: " + inviteDto.getMemberId());
        }
        
        // TeamUser 생성
        Auth auth = authRepository.findById(inviteDto.getAuthority().name())
                .orElseGet(() -> {
                    Auth newAuth = new Auth();
                    newAuth.setAuthId(inviteDto.getAuthority().name());
                    newAuth.setAuthName(inviteDto.getAuthority().getDisplayName());
                    return authRepository.save(newAuth);
                });
        
        TeamUser teamUser = new TeamUser();
        teamUser.setTeam(container.getTeam());
        teamUser.setMember(invitedMember);
        teamUser.setTeamAuth(auth);
        teamUser.setJoinedDate(LocalDateTime.now());
        teamUser.setLastActivityDate(LocalDateTime.now());
        
        container.getTeam().getTeamUsers().add(teamUser);
        
        return GroupMemberResponseDto.builder()
                .memberId(invitedMember.getMemberId())
                .memberName(invitedMember.getMemberName())
                .memberEmail(invitedMember.getMemberEmail())
                .authority(inviteDto.getAuthority())
                .joinedDate(LocalDateTime.now())
                .lastActivityDate(LocalDateTime.now())
                .build();
    }
    
    /**
     * 컨테이너 멤버 목록 조회
     * 
     * @param containerId 조회할 컨테이너 ID
     * @param memberId 조회를 요청하는 사용자 ID
     * @return 컨테이너 멤버 목록
     * @throws ContainerNotFoundException 컨테이너가 존재하지 않는 경우
     * @throws IllegalArgumentException 비공개 컨테이너에 접근 권한이 없는 경우
     */
    @Override
    public List<GroupMemberResponseDto> getContainerMembers(Long containerId, String memberId) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new ContainerNotFoundException("컨테이너를 찾을 수 없습니다: " + containerId));
        
        // 접근 권한 확인
        if (container.getVisibility() == ContainerVisibility.PRIVATE && !hasAccess(container, memberId)) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
        
        return container.getTeam().getTeamUsers().stream()
                .map(tu -> GroupMemberResponseDto.builder()
                        .teamUserId(Long.valueOf(tu.getTeamUserId()))
                        .memberId(tu.getMember().getMemberId())
                        .memberName(tu.getMember().getMemberName())
                        .memberEmail(tu.getMember().getMemberEmail())
                        .authority(Authority.valueOf(tu.getTeamAuth().getAuthId()))
                        .joinedDate(tu.getJoinedDate())
                        .lastActivityDate(tu.getLastActivityDate())
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * 멤버 권한 변경
     * 
     * @param containerId 대상 컨테이너 ID
     * @param requesterId 권한 변경을 요청하는 사용자 ID
     * @param targetMemberId 권한을 변경할 대상 멤버 ID
     * @param newAuthority 새로운 권한 (ROOT 또는 USER)
     * @throws ContainerNotFoundException 컨테이너가 존재하지 않는 경우
     * @throws UnauthorizedContainerAccessException ROOT 권한이 없는 경우
     * @throws IllegalArgumentException 소유자 권한 변경 시도 또는 멤버를 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public void updateMemberAuthority(Long containerId, String requesterId, String targetMemberId, Authority newAuthority) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new ContainerNotFoundException("컨테이너를 찾을 수 없습니다: " + containerId));
        
        // ROOT 권한 확인
        if (!hasRootAuthority(container, requesterId)) {
            throw new UnauthorizedContainerAccessException("ROOT 권한이 필요합니다.");
        }
        
        // 소유자 권한은 변경 불가
        if (container.getOwner().getMemberId().equals(targetMemberId)) {
            throw new IllegalArgumentException("소유자의 권한은 변경할 수 없습니다.");
        }
        
        TeamUser teamUser = container.getTeam().getTeamUsers().stream()
                .filter(tu -> tu.getMember().getMemberId().equals(targetMemberId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));
        
        // Auth 엔티티를 데이터베이스에서 조회
        Auth newAuth = authRepository.findById(newAuthority.name())
                .orElseThrow(() -> new IllegalArgumentException("권한을 찾을 수 없습니다: " + newAuthority.name()));
        teamUser.setTeamAuth(newAuth);
    }
    
    /**
     * 컨테이너에서 멤버 제거
     * 
     * @param containerId 대상 컨테이너 ID
     * @param requesterId 제거를 요청하는 사용자 ID
     * @param targetMemberId 제거할 대상 멤버 ID
     * @throws ContainerNotFoundException 컨테이너가 존재하지 않는 경우
     * @throws UnauthorizedContainerAccessException ROOT 권한이 없는 경우
     * @throws IllegalArgumentException 소유자를 제거하려고 하는 경우
     */
    @Override
    @Transactional
    public void removeMember(Long containerId, String requesterId, String targetMemberId) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new ContainerNotFoundException("컨테이너를 찾을 수 없습니다: " + containerId));
        
        // ROOT 권한 확인
        if (!hasRootAuthority(container, requesterId)) {
            throw new UnauthorizedContainerAccessException("ROOT 권한이 필요합니다.");
        }
        
        // 소유자는 제거 불가
        if (container.getOwner().getMemberId().equals(targetMemberId)) {
            throw new IllegalArgumentException("소유자는 제거할 수 없습니다.");
        }
        
        container.getTeam().getTeamUsers().removeIf(tu -> 
            tu.getMember().getMemberId().equals(targetMemberId));
    }
    
    /**
     * 컨테이너에서 탈퇴
     * 
     * @param containerId 탈퇴할 컨테이너 ID
     * @param memberId 탈퇴하는 사용자 ID
     * @throws ContainerNotFoundException 컨테이너가 존재하지 않는 경우
     * @throws IllegalArgumentException 소유자가 탈퇴하려고 하는 경우
     */
    @Override
    @Transactional
    public void leaveContainer(Long containerId, String memberId) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new ContainerNotFoundException("컨테이너를 찾을 수 없습니다: " + containerId));
        
        // 소유자는 탈퇴 불가
        if (container.getOwner().getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("소유자는 컨테이너를 탈퇴할 수 없습니다.");
        }
        
        container.getTeam().getTeamUsers().removeIf(tu -> 
            tu.getMember().getMemberId().equals(memberId));
    }
    
    /**
     * 멤버의 활동 시간 업데이트
     * 
     * @param containerId 대상 컨테이너 ID
     * @param memberId 활동 시간을 업데이트할 사용자 ID
     * @throws ContainerNotFoundException 컨테이너가 어재하지 않는 경우
     * @throws MemberNotFoundException 멤버를 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public void updateMemberActivity(Long containerId, String memberId) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new ContainerNotFoundException("컨테이너를 찾을 수 없습니다: " + containerId));
        
        TeamUser teamUser = container.getTeam().getTeamUsers().stream()
                .filter(tu -> tu.getMember().getMemberId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new MemberNotFoundException("멤버를 찾을 수 없습니다: " + memberId));
        
        // 활동 시간 업데이트
        teamUser.setLastActivityDate(LocalDateTime.now());
        log.info("Updated activity for member {} in container {}", memberId, containerId);
    }
    
    // Helper methods
    
    /**
     * 컨테이너 접근 권한 확인
     * 
     * @param container 확인할 컨테이너
     * @param memberId 확인할 사용자 ID
     * @return 소유자이거나 팀 멤버인 경우 true
     */
    private boolean hasAccess(Container container, String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        
        return container.getOwner().equals(member) ||
                container.getTeam().getTeamUsers().stream()
                        .anyMatch(tu -> tu.getMember().equals(member));
    }
    
    /**
     * ROOT 권한 확인
     * 
     * @param container 확인할 컨테이너
     * @param memberId 확인할 사용자 ID
     * @return 소유자이거나 ROOT 권한을 가진 경우 true
     */
    private boolean hasRootAuthority(Container container, String memberId) {
        if (container.getOwner().getMemberId().equals(memberId)) {
            return true;
        }
        
        return container.getTeam().getTeamUsers().stream()
                .filter(tu -> tu.getMember().getMemberId().equals(memberId))
                .anyMatch(tu -> "ROOT".equals(tu.getTeamAuth().getAuthId()));
    }
    
    
    
    /**
     * 사용자의 컨테이너 권한 조회
     * 
     * @param container 확인할 컨테이너
     * @param memberId 확인할 사용자 ID
     * @return 사용자의 권한 문자열 (ROOT, USER, null)
     */
    private String getUserAuthority(Container container, String memberId) {
        if (container.getOwner().getMemberId().equals(memberId)) {
            return "ROOT";
        }
        
        return container.getTeam().getTeamUsers().stream()
                .filter(tu -> tu.getMember().getMemberId().equals(memberId))
                .map(tu -> tu.getTeamAuth().getAuthId())
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 6개월 이상 비활동 멤버 자동 탈퇴
     * 매일 새벽 2시에 실행
     */
    @Override
    @Scheduled(cron = "0 0 2 * * *") // 매일 새벽 2시
    @Transactional
    public void removeInactiveMembers() {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        
        log.info("Starting inactive member cleanup. Checking members inactive since: {}", sixMonthsAgo);
        
        // 모든 컨테이너의 TeamUser를 조회하여 비활동 멤버 제거
        List<TeamUser> inactiveUsers = new ArrayList<>();
        
        containerRepository.findAll().forEach(container -> {
            List<TeamUser> toRemove = container.getTeam().getTeamUsers().stream()
                    .filter(tu -> {
                        // 소유자는 제거하지 않음
                        if (container.getOwner().getMemberId().equals(tu.getMember().getMemberId())) {
                            return false;
                        }
                        // 활동 시간이 null이거나 6개월 이상 된 경우
                        return tu.getLastActivityDate() == null || 
                               tu.getLastActivityDate().isBefore(sixMonthsAgo);
                    })
                    .collect(Collectors.toList());
            
            if (!toRemove.isEmpty()) {
                log.info("Removing {} inactive members from container: {}", 
                    toRemove.size(), container.getContainerName());
                
                toRemove.forEach(tu -> {
                    log.info("Removing inactive member {} from container {}", 
                        tu.getMember().getMemberId(), container.getContainerName());
                    container.getTeam().getTeamUsers().remove(tu);
                });
                
                inactiveUsers.addAll(toRemove);
            }
        });
        
        log.info("Inactive member cleanup completed. Total removed: {}", inactiveUsers.size());
    }
    
    /**
     * 컨테이너 검색 (QueryDSL 사용)
     * 
     * @param name 컨테이너 이름 (부분 일치, null 가능)
     * @param visibility 공개 여부 (PUBLIC/PRIVATE, null 가능)
     * @param ownerId 소유자 ID (null 가능)
     * @param memberId 권한 표시를 위한 사용자 ID (null 가능)
     * @return 검색 조건에 맞는 컨테이너 목록
     */
    @Override
    public List<ContainerResponseDto> searchContainers(String name, ContainerVisibility visibility, 
                                                      String ownerId, String memberId) {
        List<Container> containers = containerRepository.searchContainers(name, visibility, ownerId, memberId);
        
        return containers.stream()
                .map(container -> {
                    String authority = getUserAuthority(container, memberId != null ? memberId : "");
                    return ContainerResponseDto.from(container, authority, container.getTeam().getTeamUsers().size());
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 사용자의 권한별 컨테이너 통계 조회
     * 
     * @param memberId 통계를 조회할 사용자 ID
     * @return 권한별 컨테이너 개수 Map (ROOT -> n개, USER -> m개)
     * @throws IllegalArgumentException 회원이 존재하지 않는 경우
     */
    @Override
    public Map<Authority, Long> getContainerStatsByAuthority(String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        
        List<ContainerAuthorityCount> stats = containerRepository.countContainersByAuthority(member);
        
        Map<Authority, Long> result = new HashMap<>();
        for (Authority auth : Authority.values()) {
            result.put(auth, 0L);
        }
        
        stats.forEach(stat -> result.put(stat.getAuthority(), stat.getCount()));
        
        return result;
    }
    
    /**
     * 컨테이너 상세 통계 정보 조회
     * 
     * @param containerId 통계를 조회할 컨테이너 ID
     * @return 컨테이너 통계 정보 (전체 멤버 수, 활동 멤버 수, 권한별 분포 등)
     * @throws ContainerNotFoundException 컨테이너가 존재하지 않는 경우
     */
    @Override
    public ContainerStatisticsDto getContainerStatistics(Long containerId) {
        Container container = containerRepository.findContainerWithDetails(containerId)
                .orElseThrow(() -> new ContainerNotFoundException("컨테이너를 찾을 수 없습니다: " + containerId));
        
        ContainerStatistics stats = containerRepository.getContainerStatistics(containerId);
        
        // 권한별 멤버 수 계산
        Map<String, Long> authCounts = container.getTeam().getTeamUsers().stream()
                .collect(Collectors.groupingBy(
                    tu -> tu.getTeamAuth().getAuthId(),
                    Collectors.counting()
                ));
        
        return ContainerStatisticsDto.builder()
                .containerId(containerId)
                .containerName(container.getContainerName())
                .totalMemberCount(stats.getMemberCount())
                .activeMemberCount(stats.getActiveMemberCount())
                .inactiveMemberCount(stats.getMemberCount() - stats.getActiveMemberCount())
                .lastActivityDate(stats.getLastActivityDate())
                .createdDate(container.getContainerDate().atStartOfDay())
                .rootMemberCount(authCounts.getOrDefault("ROOT", 0L))
                .userMemberCount(authCounts.getOrDefault("USER", 0L))
                .build();
    }
}