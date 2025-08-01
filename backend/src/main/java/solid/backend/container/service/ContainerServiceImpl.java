package solid.backend.container.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import jakarta.persistence.OptimisticLockException;
import org.springframework.dao.OptimisticLockingFailureException;
import solid.backend.container.dto.*;
import solid.backend.entity.*;
import solid.backend.jpaRepository.ContainerRepository;
import solid.backend.jpaRepository.MemberRepository;
import solid.backend.jpaRepository.AuthRepository;
import solid.backend.jpaRepository.TeamUserRepository;
import solid.backend.container.repository.ContainerQueryRepository;
import solid.backend.container.exception.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import static solid.backend.container.constant.ContainerConstants.*;
import solid.backend.container.dto.ContainerMemberDto;

/**
 * 컨테이너 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContainerServiceImpl implements ContainerService {
    
    /** 컨테이너 데이터 접근 레포지토리 */
    private final ContainerRepository containerRepository;
    /** 컨테이너 QueryDSL 레포지토리 */
    private final ContainerQueryRepository containerQueryRepository;
    /** 멤버 데이터 접근 레포지토리 */
    private final MemberRepository memberRepository;
    /** 권한 데이터 접근 레포지토리 */
    private final AuthRepository authRepository;
    /** 팀 멤버 데이터 접근 레포지토리 */
    private final TeamUserRepository teamUserRepository;
    
    /**
     * 컨테이너 생성
     * @param memberId 사용자 ID
     * @param createDto 컨테이너 생성 정보
     * @return 생성된 컨테이너 정보
     * @throws MemberNotFoundException 회원을 찾을 수 없는 경우
     * @throws InvalidMemberException 초대할 회원이 존재하지 않는 경우
     */
    @Override
    @Transactional
    public ContainerResponseDto createContainer(String memberId, ContainerCreateDto createDto) {
        // null 체크
        validateMemberId(memberId, ERROR_MEMBER_ID_REQUIRED);
        
        // 컨테이너 생성자 확인
        Member creator = getMemberOrThrow(memberId);
        
        // 팀 생성
        Team team = new Team();
        team.setTeamName(createDto.getContainerName());
        
        // 컨테이너 생성
        Container container = Container.builder()
                .containerName(createDto.getContainerName())
                .containerContent(createDto.getContainerContent())
                .containerAuth(createDto.getIsPublic())
                .team(team)
                .build();
        
        Container savedContainer = containerRepository.save(container);
        
        // 생성자를 팀에 ROOT 권한으로 추가
        Auth rootAuth = getOrCreateAuth(AUTHORITY_ROOT, "관리자");
        
        TeamUser creatorMember = createTeamUser(team, creator, rootAuth);
        team.getTeamUsers().add(creatorMember);
        
        // 초대 멤버 처리
        if (createDto.getInvitedMemberIds() != null && !createDto.getInvitedMemberIds().isEmpty()) {
            validateAndAddInvitedMembers(team, createDto.getInvitedMemberIds());
        }
        
        return createContainerResponse(savedContainer, memberId);
    }
    
    /**
     * 컨테이너 상세 조회
     * @param containerId 컨테이너 ID
     * @param memberId 사용자 ID
     * @return 컨테이너 상세 정보
     * @throws ContainerNotFoundException 컨테이너를 찾을 수 없는 경우
     * @throws IllegalArgumentException 접근 권한이 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public ContainerResponseDto getContainer(Integer containerId, String memberId) {
        // QueryDSL을 사용하여 연관 데이터를 한번에 조회
        Container container = containerQueryRepository.findByIdWithTeam(containerId)
                .orElseThrow(() -> new ContainerNotFoundException(ERROR_CONTAINER_NOT_FOUND + containerId));
        
        // 접근 권한 확인 (비공개 컨테이너인 경우)
        if (!container.getContainerAuth()) { // false = 비공개
            // memberId가 null인 경우 비공개 컨테이너 접근 불가
            validateMemberId(memberId, ERROR_PRIVATE_CONTAINER_ACCESS_DENIED);
            
            Member member = getMemberOrThrow(memberId);
            
            // 팀 멤버인 경우만 접근 가능
            boolean hasAccess = container.getTeam().getTeamUsers().stream()
                    .anyMatch(tu -> tu.getMember().equals(member));
            
            if (!hasAccess) {
                throw new IllegalArgumentException(ERROR_NO_ACCESS_PERMISSION);
            }
        }
        
        // 사용자 권한 조회 및 응답 생성
        return createContainerResponse(container, memberId);
    }
    
    /**
     * 컨테이너 정보 업데이트
     * @param containerId 컨테이너 ID
     * @param memberId 사용자 ID
     * @param updateDto 업데이트 정보
     * @return 업데이트된 컨테이너 정보
     * @throws ContainerNotFoundException 컨테이너를 찾을 수 없는 경우
     * @throws UnauthorizedContainerAccessException 권한이 없는 경우
     */
    @Override
    @Transactional
    public ContainerResponseDto updateContainer(Integer containerId, String memberId, ContainerUpdateDto updateDto) {
        Container container = getContainerWithTeamOrThrow(containerId);
        
        // 수정 권한 확인
        TeamUser teamUser = getTeamUserOrThrow(container, memberId);
        
        String userAuth = teamUser.getTeamAuth().getAuthId();
        // ROOT와 USER 모두 수정 가능
        
        // 컨테이너 설정 변경은 ROOT만 가능
        if (updateDto.getIsPublic() != null && !AUTHORITY_ROOT.equals(userAuth)) {
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
        if (updateDto.getIsPublic() != null) {
            container.setContainerAuth(updateDto.getIsPublic());
        }
        
        return ContainerResponseDto.from(container, userAuth, container.getTeam().getTeamUsers().size());
    }
    
    /**
     * 컨테이너 삭제
     * @param containerId 컨테이너 ID
     * @param memberId 사용자 ID
     * @throws ContainerNotFoundException 컨테이너를 찾을 수 없는 경우
     * @throws UnauthorizedContainerAccessException 권한이 없는 경우
     */
    @Override
    @Transactional
    public void deleteContainer(Integer containerId, String memberId) {
        Container container = getContainerWithTeamOrThrow(containerId);
        
        // ROOT 권한 확인
        requireRootAuthority(container, memberId, "컨테이너 삭제는 관리자만 가능합니다.");
        
        containerRepository.delete(container);
    }
    
    /**
     * ROOT 권한을 요구하고 없으면 예외 발생
     * @param container 대상 컨테이너
     * @param memberId 확인할 사용자 ID
     * @param errorMessage 권한이 없을 때 표시할 메시지
     * @throws UnauthorizedContainerAccessException 권한이 없는 경우
     */
    private void requireRootAuthority(Container container, String memberId, String errorMessage) {
        if (!hasRootAuthority(container, memberId)) {
            throw new UnauthorizedContainerAccessException(errorMessage);
        }
    }
    
    /**
     * 사용자가 소유한 컨테이너 목록 조회
     * @param memberId 사용자 ID
     * @return 소유한 컨테이너 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<ContainerResponseDto> getMyContainers(String memberId) {
        Member member = getMemberOrThrow(memberId);
        
        // Get containers where member has ROOT authority
        List<Container> ownedContainers = containerQueryRepository.findContainersByMemberWithAuthority(member)
                .stream()
                .filter(container -> container.getTeam().getTeamUsers().stream()
                        .anyMatch(tu -> tu.getMember().equals(member) && AUTHORITY_ROOT.equals(tu.getTeamAuth().getAuthId())))
                .toList();
                
        return convertToResponseDtoList(ownedContainers, memberId);
    }
    
    /**
     * 사용자가 참여중인 컨테이너 목록 조회
     * @param memberId 사용자 ID
     * @return 참여중인 컨테이너 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<ContainerResponseDto> getSharedContainers(String memberId) {
        Member member = getMemberOrThrow(memberId);
        
        return convertToResponseDtoList(containerQueryRepository.findSharedContainers(member), memberId);
    }
    
    /**
     * 모든 공개 컨테이너 목록 조회
     * 사용자가 이미 참여중인 컨테이너는 제외됩니다.
     * @param memberId 사용자 ID (nullable)
     * @return 공개 컨테이너 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<ContainerResponseDto> getPublicContainers(String memberId) {
        // QueryDSL을 사용하여 데이터베이스 레벨에서 필터링
        List<Container> publicContainers = containerQueryRepository.findPublicContainersExcludingMember(memberId);
        return convertToResponseDtoList(publicContainers, memberId);
    }
    
    /**
     * 사용자가 접근 가능한 모든 컨테이너 목록 조회
     * @param memberId 사용자 ID
     * @return 접근 가능한 컨테이너 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<ContainerResponseDto> getAllAccessibleContainers(String memberId) {
        Member member = getMemberOrThrow(memberId);
        
        return convertToResponseDtoList(containerQueryRepository.findAllAccessibleContainers(member), memberId);
    }
    
    /**
     * 컨테이너에 멤버 초대
     * @param containerId 컨테이너 ID
     * @param requesterId 요청자 ID
     * @param inviteDto 초대할 멤버 정보
     * @return 초대된 멤버 정보
     * @throws ContainerNotFoundException 컨테이너를 찾을 수 없는 경우
     * @throws UnauthorizedContainerAccessException 권한이 없는 경우
     * @throws MemberNotFoundException 회원을 찾을 수 없는 경우
     * @throws DuplicateMemberException 이미 멤버인 경우
     */
    @Override
    @Transactional
    public GroupMemberResponseDto inviteMember(Integer containerId, String requesterId, MemberInviteDto inviteDto) {
        Container container = getContainerWithTeamOrThrow(containerId);
        
        // ROOT 권한 확인
        requireRootAuthority(container, requesterId, ERROR_ROOT_AUTHORITY_REQUIRED);
        
        Member invitedMember = memberRepository.findById(inviteDto.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException(ERROR_INVITED_MEMBER_NOT_FOUND + inviteDto.getMemberId()));
        
        // 이미 멤버인지 확인
        boolean alreadyMember = container.getTeam().getTeamUsers().stream()
                .anyMatch(tu -> tu.getMember().equals(invitedMember));
        
        if (alreadyMember) {
            throw new DuplicateMemberException("이미 멤버입니다: " + inviteDto.getMemberId());
        }
        
        // TeamUser 생성 - 초대된 멤버는 항상 USER 권한
        Auth auth = getOrCreateAuth(AUTHORITY_USER, "사용자");
        
        TeamUser teamUser = createTeamUser(container.getTeam(), invitedMember, auth);
        container.getTeam().getTeamUsers().add(teamUser);
        
        return GroupMemberResponseDto.builder()
                .memberId(invitedMember.getMemberId())
                .memberName(invitedMember.getMemberName())
                .memberEmail(invitedMember.getMemberEmail())
                .authority(AUTHORITY_USER)
                .joinedDate(LocalDateTime.now())
                .lastActivityDate(LocalDateTime.now())
                .build();
    }
    
    /**
     * 컨테이너 멤버 목록 조회
     * @param containerId 컨테이너 ID
     * @param memberId 사용자 ID
     * @return 멤버 목록
     * @throws ContainerNotFoundException 컨테이너를 찾을 수 없는 경우
     * @throws IllegalArgumentException 접근 권한이 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public List<GroupMemberResponseDto> getContainerMembers(Integer containerId, String memberId) {
        Container container = getContainerWithTeamOrThrow(containerId);
        
        // 접근 권한 확인 (비공개 컨테이너인 경우)
        if (!container.getContainerAuth() && !hasAccess(container, memberId)) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
        
        return container.getTeam().getTeamUsers().stream()
                .map(tu -> GroupMemberResponseDto.builder()
                        .teamUserId(tu.getTeamUserId() != null ? tu.getTeamUserId().longValue() : null)
                        .memberId(tu.getMember().getMemberId())
                        .memberName(tu.getMember().getMemberName())
                        .memberEmail(tu.getMember().getMemberEmail())
                        .authority(tu.getTeamAuth() != null ? tu.getTeamAuth().getAuthId() : AUTHORITY_USER)
                        .joinedDate(tu.getJoinedDate())
                        .lastActivityDate(tu.getLastActivityDate())
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * 컨테이너에서 멤버 제거
     * @param containerId 컨테이너 ID
     * @param requesterId 요청자 ID
     * @param targetMemberId 제거할 멤버 ID
     * @throws ContainerNotFoundException 컨테이너를 찾을 수 없는 경우
     * @throws UnauthorizedContainerAccessException 권한이 없는 경우
     * @throws IllegalArgumentException 소유자를 제거하려는 경우
     */
    @Override
    @Transactional
    public void removeMember(Integer containerId, String requesterId, String targetMemberId) {
        Container container = getContainerWithTeamOrThrow(containerId);
        
        // ROOT 권한 확인
        requireRootAuthority(container, requesterId, ERROR_ROOT_AUTHORITY_REQUIRED);
        
        // ROOT 권한자는 제거 불가
        if (isContainerOwner(container, getMemberOrThrow(targetMemberId))) {
            throw new IllegalArgumentException(ERROR_OWNER_CANNOT_BE_REMOVED);
        }
        
        removeTeamMember(container.getTeam(), targetMemberId);
    }
    
    /**
     * 컨테이너에서 탈퇴
     * @param containerId 컨테이너 ID
     * @param memberId 사용자 ID
     * @throws ContainerNotFoundException 컨테이너를 찾을 수 없는 경우
     * @throws IllegalArgumentException 소유자가 탈퇴하려는 경우
     */
    @Override
    @Transactional
    public void leaveContainer(Integer containerId, String memberId) {
        Container container = getContainerWithTeamOrThrow(containerId);
        
        // ROOT 권한자는 탈퇴 불가
        if (isContainerOwner(container, getMemberOrThrow(memberId))) {
            throw new IllegalArgumentException(ERROR_OWNER_CANNOT_LEAVE);
        }
        
        removeTeamMember(container.getTeam(), memberId);
    }
    
    /**
     * 멤버의 활동 시간 업데이트
     * @param containerId 컨테이너 ID
     * @param memberId 사용자 ID
     * @throws ContainerNotFoundException 컨테이너를 찾을 수 없는 경우
     * @throws MemberNotFoundException 멤버를 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public void updateMemberActivity(Integer containerId, String memberId) {
        Container container = getContainerWithTeamOrThrow(containerId);
        
        TeamUser teamUser = getTeamUserOrThrow(container, memberId);
        
        // 활동 시간 업데이트
        teamUser.setLastActivityDate(LocalDateTime.now());
        log.info("Updated activity for member {} in container {}", memberId, containerId);
    }
    
    // Helper methods
    
    /**
     * 사용자가 컨테이너에 접근 가능한지 확인
     * @param container 대상 컨테이너
     * @param memberId 확인할 사용자 ID
     * @return 접근 가능 여부
     */
    private boolean hasAccess(Container container, String memberId) {
        Member member = getMemberOrThrow(memberId);
        
        return container.getTeam().getTeamUsers().stream()
                .anyMatch(tu -> tu.getMember().equals(member));
    }
    
    /**
     * 사용자가 컨테이너에 대한 ROOT 권한을 가지고 있는지 확인
     * @param container 대상 컨테이너
     * @param memberId 확인할 사용자 ID
     * @return ROOT 권한 보유 여부
     */
    private boolean hasRootAuthority(Container container, String memberId) {
        return container.getTeam().getTeamUsers().stream()
                .filter(tu -> tu.getMember().getMemberId().equals(memberId))
                .anyMatch(tu -> AUTHORITY_ROOT.equals(tu.getTeamAuth().getAuthId()));
    }
    
    /**
     * 특정 컨테이너에서 사용자의 권한을 조회
     * @param container 대상 컨테이너
     * @param memberId 사용자 ID
     * @return 권한 문자열 (ROOT/USER) 또는 null
     */
    private String getUserAuthority(Container container, String memberId) {
        
        return container.getTeam().getTeamUsers().stream()
                .filter(tu -> tu.getMember().getMemberId().equals(memberId))
                .map(tu -> tu.getTeamAuth().getAuthId())
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 공개 컨테이너 참여
     * @param containerId 컨테이너 ID
     * @param memberId 참여하는 사용자 ID
     * @return 참여 결과 정보
     * @throws ContainerNotFoundException 컨테이너를 찾을 수 없는 경우
     * @throws IllegalArgumentException 비공개 컨테이너인 경우
     * @throws IllegalStateException 이미 멤버인 경우
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public synchronized GroupMemberResponseDto joinContainer(Integer containerId, String memberId) {
        // 컨테이너 조회
        Container container = getContainerWithTeamOrThrow(containerId);
        
        // 공개 여부 확인
        if (!container.getContainerAuth()) {
            throw new IllegalArgumentException("비공개 컨테이너에는 참여할 수 없습니다");
        }
        
        // 사용자 조회
        Member member = getMemberOrThrow(memberId);
        
        // 이미 멤버인지 확인
        boolean isAlreadyMember = container.getTeam().getTeamUsers().stream()
                .anyMatch(tu -> tu.getMember().getMemberId().equals(memberId));
                
        if (isAlreadyMember) {
            throw new IllegalStateException("이미 해당 컨테이너의 멤버입니다");
        }
        
        // USER 권한으로 팀에 추가
        Auth userAuth = authRepository.findById(AUTHORITY_USER)
                .orElseThrow(() -> new RuntimeException("USER 권한이 존재하지 않습니다"));
                
        TeamUser teamUser = new TeamUser();
        teamUser.setMember(member);
        teamUser.setTeam(container.getTeam());
        teamUser.setTeamAuth(userAuth);
        teamUser.setJoinedDate(LocalDateTime.now());
        teamUser.setLastActivityDate(LocalDateTime.now());
        
        // TeamUser를 직접 저장하는 것이 더 안전함
        try {
            teamUser = teamUserRepository.save(teamUser);
            
            // 저장된 TeamUser를 컬렉션에 추가
            container.getTeam().getTeamUsers().add(teamUser);
        } catch (OptimisticLockException | OptimisticLockingFailureException e) {
            log.warn("동시성 충돌 발생: 다른 사용자가 동시에 컨테이너에 참여하려고 시도했습니다. containerId: {}, memberId: {}", containerId, memberId);
            throw new IllegalStateException("다른 사용자가 동시에 작업 중입니다. 잠시 후 다시 시도해주세요.", e);
        }
        
        // Lazy Loading 문제를 해결하기 위해 fetch join으로 다시 조회
        try {
            teamUser = teamUserRepository.findByIdWithFetch(teamUser.getTeamUserId())
                    .orElseThrow(() -> new IllegalStateException("저장된 TeamUser를 찾을 수 없습니다"));
        } catch (Exception e) {
            log.error("TeamUser 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("팀 멤버 정보 조회 중 오류가 발생했습니다", e);
        }
        
        log.info("Member {} joined public container {}", memberId, containerId);
        
        return GroupMemberResponseDto.from(teamUser);
    }
    
    /**
     * 6개월 이상 비활동 멤버 자동 탈퇴
     */
    @Override
    @Scheduled(cron = INACTIVE_MEMBER_CLEANUP_SCHEDULE) // 매일 새벽 2시
    @Transactional
    public void removeInactiveMembers() {
        try {
            LocalDateTime inactiveThreshold = LocalDateTime.now().minusMonths(INACTIVE_MONTHS);
        
            log.info("Starting inactive member cleanup. Checking members inactive since: {}", inactiveThreshold);
        
        // 모든 컨테이너의 TeamUser를 조회하여 비활동 멤버 제거
        List<TeamUser> inactiveUsers = new ArrayList<>();
        
        containerRepository.findAll().forEach(container -> {
            List<TeamUser> toRemove = container.getTeam().getTeamUsers().stream()
                    .filter(tu -> {
                        // ROOT 권한자는 제거하지 않음
                        if (AUTHORITY_ROOT.equals(tu.getTeamAuth().getAuthId())) {
                            return false;
                        }
                        // 활동 시간이 null이거나 기준일 이상 된 경우
                        LocalDateTime lastActivity = tu.getLastActivityDate();
                        return lastActivity == null || lastActivity.isBefore(inactiveThreshold);
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
        } catch (Exception e) {
            log.error("Error during inactive member cleanup", e);
            // 예외가 발생해도 서비스는 계속 실행
        }
    }
    
    /*
    // 컨테이너 검색
    @Override
    @Transactional(readOnly = true)
    public List<ContainerResponseDto> searchContainers(String name, Boolean isPublic, 
                                                      String ownerId, String memberId) {
        Member member = memberId != null ? getMemberOrThrow(memberId) : null;
        List<Container> containers = containerQueryRepository.searchContainers(name, isPublic, ownerId, member);
        return convertToResponseDtoList(containers, memberId);
    }
    */
    
    /*
    // 사용자의 권한별 컨테이너 통계 조회
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getContainerStatsByAuthority(String memberId) {
        Member member = getMemberOrThrow(memberId);
        
        // 권한별 통계 계산
        List<Container> allContainers = containerQueryRepository.findContainersByMemberWithAuthority(member);
        long ownedCount = allContainers.stream()
                .filter(container -> container.getTeam().getTeamUsers().stream()
                        .anyMatch(tu -> tu.getMember().equals(member) && AUTHORITY_ROOT.equals(tu.getTeamAuth().getAuthId())))
                .count();
        List<Container> sharedContainers = containerQueryRepository.findSharedContainers(member);
        
        Map<String, Long> result = new HashMap<>();
        result.put(AUTHORITY_ROOT, ownedCount);
        result.put(AUTHORITY_USER, (long) sharedContainers.size());
        
        return result;
    }
    */
    
    /*
    // 컨테이너 상세 통계 정보 조회
    @Override
    @Transactional(readOnly = true)
    public ContainerStatisticsDto getContainerStatistics(Integer containerId) {
        Container container = containerQueryRepository.findByIdWithTeam(containerId)
                .orElseThrow(() -> new ContainerNotFoundException(ERROR_CONTAINER_NOT_FOUND + containerId));
        
        // 통계 계산
        Team team = container.getTeam();
        long memberCount = team.getTeamUsers().size();
        


         long activeMemberCount = team.getTeamUsers().stream()
                 .map(TeamUser::getMember)
                 .filter(Objects::nonNull)
                 .filter(Member::isMemberIsOnline)
                 .count();

        
        LocalDateTime lastActivityDate = team.getTeamUsers().stream()
                .map(TeamUser::getLastActivityDate)
                .filter(date -> date != null)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        
        // 권한별 멤버 수 계산
        Map<String, Long> authCounts = container.getTeam().getTeamUsers().stream()
                .collect(Collectors.groupingBy(
                    tu -> tu.getTeamAuth().getAuthId(),
                    Collectors.counting()
                ));
        
        // 멤버 리스트 생성 (접속 여부 포함)
        List<ContainerMemberDto> memberList = team.getTeamUsers().stream()
                .map(teamUser -> {
                    Member member = teamUser.getMember();
                    return ContainerMemberDto.builder()
                            .memberId(member.getMemberId())
                            .memberName(member.getMemberName())
                            .isOnline(member.getMemberIsOnline())
                            .authority(teamUser.getTeamAuth().getAuthId())
                            .lastActivityDate(teamUser.getLastActivityDate())
                            .joinedDate(teamUser.getJoinedDate())
                            .build();
                })
                .sorted((a, b) -> {
                    // 온라인 사용자를 먼저 정렬
                    if (a.getIsOnline() != b.getIsOnline()) {
                        return b.getIsOnline() ? 1 : -1;
                    }
                    // 그 다음 권한별 정렬 (ROOT 먼저)
                    if (!a.getAuthority().equals(b.getAuthority())) {
                        return AUTHORITY_ROOT.equals(a.getAuthority()) ? -1 : 1;
                    }
                    // 마지막으로 이름순 정렬
                    return a.getMemberName().compareTo(b.getMemberName());
                })
                .collect(Collectors.toList());
        
        return ContainerStatisticsDto.builder()
                .containerId(containerId)
                .containerName(container.getContainerName())
                .totalMemberCount(memberCount)
                .activeMemberCount(activeMemberCount)
                .inactiveMemberCount(memberCount - activeMemberCount)
                .lastActivityDate(lastActivityDate)
                .createdDate(container.getContainerDate().atStartOfDay())
                .rootMemberCount(authCounts.getOrDefault(AUTHORITY_ROOT, 0L))
                .userMemberCount(authCounts.getOrDefault(AUTHORITY_USER, 0L))
                .members(memberList)
                .build();
    }
    */
    
    // 추가 헬퍼 메서드들
    
    /**
     * 멤버를 조회하고 없으면 예외 발생
     * @param memberId 조회할 멤버 ID
     * @return 조회된 멤버 엔티티
     * @throws MemberNotFoundException 멤버를 찾을 수 없는 경우
     */
    private Member getMemberOrThrow(String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(ERROR_MEMBER_NOT_FOUND + memberId));
    }
    
    /**
     * 컨테이너를 조회하고 없으면 예외 발생
     * @param containerId 조회할 컨테이너 ID
     * @return 조회된 컨테이너 엔티티
     * @throws ContainerNotFoundException 컨테이너를 찾을 수 없는 경우
     */
    private Container getContainerOrThrow(Integer containerId) {
        return containerRepository.findById(containerId)
                .orElseThrow(() -> new ContainerNotFoundException(ERROR_CONTAINER_NOT_FOUND + containerId));
    }
    
    /**
     * 컨테이너를 팀 정보와 함께 조회하고 없으면 예외 발생
     * @param containerId 조회할 컨테이너 ID
     * @return 조회된 컨테이너 엔티티 (팀 정보 포함)
     * @throws ContainerNotFoundException 컨테이너를 찾을 수 없는 경우
     */
    private Container getContainerWithTeamOrThrow(Integer containerId) {
        return containerQueryRepository.findByIdWithTeam(containerId)
                .orElseThrow(() -> new ContainerNotFoundException(ERROR_CONTAINER_NOT_FOUND + containerId));
    }
    
    /**
     * 권한을 조회하고 없으면 생성
     * @param authId 권한 ID (ROOT/USER)
     * @param authName 권한 명칭
     * @return 조회 또는 생성된 권한 엔티티
     */
    private Auth getOrCreateAuth(String authId, String authName) {
        return authRepository.findById(authId)
                .orElseGet(() -> {
                    Auth newAuth = new Auth();
                    newAuth.setAuthId(authId);
                    newAuth.setAuthName(authName);
                    return authRepository.save(newAuth);
                });
    }
    
    /**
     * 컨테이너의 팀 멤버를 조회하고 없으면 예외 발생
     * @param container 대상 컨테이너
     * @param memberId 조회할 멤버 ID
     * @return 조회된 TeamUser 엔티티
     * @throws UnauthorizedContainerAccessException 멤버가 아닌 경우
     */
    private TeamUser getTeamUserOrThrow(Container container, String memberId) {
        return container.getTeam().getTeamUsers().stream()
                .filter(tu -> tu.getMember().getMemberId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedContainerAccessException(ERROR_NOT_CONTAINER_MEMBER));
    }
    
    /**
     * 컨테이너 엔티티를 응답 DTO로 변환
     * @param container 변환할 컨테이너 엔티티
     * @param memberId 현재 사용자 ID (null 가능)
     * @return 변환된 컨테이너 응답 DTO
     */
    private ContainerResponseDto createContainerResponse(Container container, String memberId) {
        String authority = (memberId != null) ? getUserAuthority(container, memberId) : null;
        return ContainerResponseDto.from(container, authority, container.getTeam().getTeamUsers().size());
    }
    
    // 페이징 지원 메서드 구현
    
    /*
    // 컨테이너 고급 검색
    @Override
    @Transactional(readOnly = true)
    public List<ContainerResponseDto> advancedSearch(ContainerSearchDto searchDto, String memberId) {
        // 검색 멤버 결정
        Member searchMember = searchDto.getMemberId() != null ? 
                memberRepository.findById(searchDto.getMemberId()).orElse(null) : null;
        
        List<Container> containers = containerQueryRepository.searchContainers(
                searchDto.getName(), 
                searchDto.getIsPublic(), 
                searchDto.getOwnerId(), 
                searchMember
        );
        
        return convertToResponseDtoList(containers, memberId);
    }
    */
    
    /**
     * 배치 작업 - 여러 컨테이너의 공개 상태 변경
     * @param containerIds 컨테이너 ID 목록
     * @param isPublic 공개 상태
     * @param requesterId 요청자 ID
     * @return 업데이트된 컨테이너 수
     * @throws UnauthorizedContainerAccessException 권한이 없는 경우
     */
    @Override
    @Transactional
    public long batchUpdateVisibility(List<Integer> containerIds, Boolean isPublic, String requesterId) {
        if (containerIds == null || containerIds.isEmpty()) {
            return 0;
        }
        
        // 각 컨테이너에 대한 권한 확인
        List<Integer> authorizedContainerIds = new ArrayList<>();
        
        for (Integer containerId : containerIds) {
            try {
                Container container = getContainerWithTeamOrThrow(containerId);
                if (hasRootAuthority(container, requesterId)) {
                    authorizedContainerIds.add(containerId);
                }
            } catch (ContainerNotFoundException e) {
                // 존재하지 않는 컨테이너는 무시
                log.warn("Container not found during batch update: {}", containerId);
            }
        }
        
        if (authorizedContainerIds.isEmpty()) {
            return 0;
        }
        
        // 배치 업데이트 실행
        return containerQueryRepository.updateContainerVisibility(authorizedContainerIds, isPublic);
    }
    
    /**
     * 멤버 ID의 유효성을 검증
     * @param memberId 검증할 멤버 ID
     * @param errorMessage 유효하지 않을 때 표시할 메시지
     * @throws IllegalArgumentException 멤버 ID가 null이거나 빈 문자열인 경우
     */
    private void validateMemberId(String memberId, String errorMessage) {
        if (memberId == null || memberId.trim().isEmpty()) {
            if (errorMessage.equals(ERROR_PRIVATE_CONTAINER_ACCESS_DENIED)) {
                throw new UnauthorizedContainerAccessException(errorMessage);
            }
            throw new IllegalArgumentException(errorMessage);
        }
    }
    
    /**
     * 컨테이너 리스트를 응답 DTO 리스트로 변환
     * @param containers 변환할 컨테이너 리스트
     * @param memberId 현재 사용자 ID (null 가능)
     * @return 변환된 DTO 리스트
     */
    private List<ContainerResponseDto> convertToResponseDtoList(List<Container> containers, String memberId) {
        return containers.stream()
                .map(container -> createContainerResponse(container, memberId))
                .collect(Collectors.toList());
    }
    
    /**
     * TeamUser 엔티티 생성 헬퍼 메서드
     * @param team 팀
     * @param member 멤버
     * @param auth 권한
     * @return 생성된 TeamUser
     */
    private TeamUser createTeamUser(Team team, Member member, Auth auth) {
        TeamUser teamUser = new TeamUser();
        teamUser.setTeam(team);
        teamUser.setMember(member);
        teamUser.setTeamAuth(auth);
        teamUser.setJoinedDate(LocalDateTime.now());
        teamUser.setLastActivityDate(LocalDateTime.now());
        return teamUser;
    }
    
    /**
     * 초대 멤버 유효성 검증 및 추가
     * @param team 대상 팀
     * @param invitedMemberIds 초대할 멤버 ID 목록
     * @throws InvalidMemberException 존재하지 않는 멤버가 있는 경우
     */
    private void validateAndAddInvitedMembers(Team team, List<String> invitedMemberIds) {
        // 존재하지 않는 멤버 확인
        List<String> invalidMemberIds = invitedMemberIds.stream()
                .filter(id -> !memberRepository.existsById(id))
                .collect(Collectors.toList());
        
        if (!invalidMemberIds.isEmpty()) {
            throw new InvalidMemberException("존재하지 않는 멤버 ID: " + String.join(", ", invalidMemberIds));
        }
        
        // 기본값은 USER 권한으로 설정
        Auth userAuth = getOrCreateAuth(AUTHORITY_USER, "사용자");
        
        for (String invitedMemberId : invitedMemberIds) {
            Member invitedMember = getMemberOrThrow(invitedMemberId);
            TeamUser teamUser = createTeamUser(team, invitedMember, userAuth);
            team.getTeamUsers().add(teamUser);
        }
    }
    
    /**
     * 팀에서 멤버 제거
     * @param team 대상 팀
     * @param memberId 제거할 멤버 ID
     */
    private void removeTeamMember(Team team, String memberId) {
        TeamUser toRemove = team.getTeamUsers().stream()
            .filter(tu -> tu.getMember().getMemberId().equals(memberId))
            .findFirst()
            .orElse(null);
            
        if (toRemove != null) {
            team.getTeamUsers().remove(toRemove);
            toRemove.setTeam(null);
        }
    }
    
    /**
     * 사용자가 컨테이너의 소유자(ROOT 권한)인지 확인
     * @param container 대상 컨테이너
     * @param member 확인할 멤버
     * @return 소유자 여부
     */
    private boolean isContainerOwner(Container container, Member member) {
        return container.getTeam().getTeamUsers().stream()
                .anyMatch(tu -> tu.getMember().equals(member) 
                        && AUTHORITY_ROOT.equals(tu.getTeamAuth().getAuthId()));
    }
    
    /**
     * 컨테이너의 팀 ID 조회
     * @param containerId 컨테이너 ID
     * @param memberId 조회하는 사용자 ID
     * @return 팀 ID
     * @throws ContainerNotFoundException 컨테이너를 찾을 수 없는 경우
     * @throws UnauthorizedContainerAccessException 접근 권한이 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public Integer getContainerTeamId(Integer containerId, String memberId) {
        // 컨테이너 조회
        Container container = getContainerWithTeamOrThrow(containerId);
        
        // 공개 컨테이너이거나 멤버인 경우에만 접근 가능
        if (!container.getContainerAuth()) {
            // 비공개 컨테이너인 경우 멤버 확인
            validateMemberId(memberId, ERROR_PRIVATE_CONTAINER_ACCESS_DENIED);
            
            if (!hasAccess(container, memberId)) {
                throw new UnauthorizedContainerAccessException(ERROR_NO_ACCESS_PERMISSION);
            }
        }
        
        // 팀 ID 반환
        return container.getTeam().getTeamId();
    }
    
}
