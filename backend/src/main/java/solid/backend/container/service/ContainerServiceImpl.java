package solid.backend.container.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solid.backend.container.dto.*;
import solid.backend.entity.Container;
import solid.backend.entity.Group;
import solid.backend.entity.GroupMember;
import solid.backend.entity.Member;
import solid.backend.common.enums.Authority;
import solid.backend.common.enums.ContainerVisibility;
import solid.backend.jpaRepository.ContainerRepository;
import solid.backend.jpaRepository.GroupMemberRepository;
import solid.backend.jpaRepository.GroupRepository;
import solid.backend.jpaRepository.MemberRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberRepository memberRepository;
    
    @Override
    @Transactional
    public ContainerResponseDto createContainer(String memberId, ContainerCreateDto createDto) {
        // 컨테이너 소유자 확인
        Member owner = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        
        // 그룹 생성
        Group group = Group.builder()
                .groupName(createDto.getContainerName())
                .build();
        final Group savedGroup = groupRepository.save(group);
        
        // 컨테이너 생성
        Container container = Container.builder()
                .containerName(createDto.getContainerName())
                .containerContent(createDto.getContainerContent())
                .visibility(createDto.getVisibility())
                .owner(owner)
                .group(savedGroup)
                .build();
        container = containerRepository.save(container);
        
        // 소유자를 ROOT 권한으로 그룹에 추가
        GroupMember ownerMember = GroupMember.builder()
                .group(savedGroup)
                .member(owner)
                .authority(Authority.ROOT)
                .build();
        groupMemberRepository.save(ownerMember);
        
        // 초대된 멤버들을 USER 권한으로 추가
        if (createDto.getInvitedMemberIds() != null && !createDto.getInvitedMemberIds().isEmpty()) {
            for (String invitedMemberId : createDto.getInvitedMemberIds()) {
                memberRepository.findById(invitedMemberId).ifPresent(invitedMember -> {
                    GroupMember groupMember = GroupMember.builder()
                            .group(savedGroup)
                            .member(invitedMember)
                            .authority(Authority.USER)
                            .build();
                    groupMemberRepository.save(groupMember);
                });
            }
        }
        
        return ContainerResponseDto.from(container, Authority.ROOT.name(), 
                createDto.getInvitedMemberIds() != null ? createDto.getInvitedMemberIds().size() + 1 : 1);
    }
    
    @Override
    public ContainerResponseDto getContainer(Long containerId, String memberId) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new IllegalArgumentException("컨테이너를 찾을 수 없습니다."));
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        
        if (container.getVisibility() == ContainerVisibility.PRIVATE) {
            boolean hasAccess = container.getOwner().getMemberId().equals(memberId) ||
                    groupMemberRepository.existsByGroupAndMember(container.getGroup(), member);
            
            if (!hasAccess) {
                throw new IllegalArgumentException("접근 권한이 없습니다.");
            }
        }
        
        GroupMember groupMember = groupMemberRepository.findByGroupAndMember(container.getGroup(), member)
                .orElse(null);
        String authority = groupMember != null ? groupMember.getAuthority().name() : null;
        
        int memberCount = groupMemberRepository.findByGroup(container.getGroup()).size();
        
        return ContainerResponseDto.from(container, authority, memberCount);
    }
    
    @Override
    public List<ContainerResponseDto> getMyContainers(String memberId) {
        Member owner = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        
        return containerRepository.findByOwner(owner).stream()
                .map(container -> {
                    int memberCount = groupMemberRepository.findByGroup(container.getGroup()).size();
                    return ContainerResponseDto.from(container, Authority.ROOT.name(), memberCount);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ContainerResponseDto> getSharedContainers(String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        
        return containerRepository.findSharedContainers(member).stream()
                .filter(container -> !container.getOwner().getMemberId().equals(memberId))
                .map(container -> {
                    GroupMember groupMember = groupMemberRepository.findByGroupAndMember(container.getGroup(), member)
                            .orElse(null);
                    String authority = groupMember != null ? groupMember.getAuthority().name() : null;
                    int memberCount = groupMemberRepository.findByGroup(container.getGroup()).size();
                    return ContainerResponseDto.from(container, authority, memberCount);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ContainerResponseDto> getPublicContainers() {
        return containerRepository.findByContainerAuth(true).stream() // true = PUBLIC
                .map(container -> {
                    int memberCount = groupMemberRepository.findByGroup(container.getGroup()).size();
                    return ContainerResponseDto.from(container, null, memberCount);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ContainerResponseDto> getAllAccessibleContainers(String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        
        return containerRepository.findAllAccessibleContainers(member).stream()
                .map(container -> {
                    GroupMember groupMember = groupMemberRepository.findByGroupAndMember(container.getGroup(), member)
                            .orElse(null);
                    String authority = groupMember != null ? groupMember.getAuthority().name() : 
                            (container.getOwner().getMemberId().equals(memberId) ? Authority.ROOT.name() : null);
                    int memberCount = groupMemberRepository.findByGroup(container.getGroup()).size();
                    return ContainerResponseDto.from(container, authority, memberCount);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public ContainerResponseDto updateContainer(Long containerId, String memberId, ContainerUpdateDto updateDto) {
        Container container = containerRepository.findByContainerIdAndOwner(containerId, 
                memberRepository.findById(memberId)
                        .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다.")))
                .orElseThrow(() -> new IllegalArgumentException("수정 권한이 없습니다."));
        
        if (updateDto.getContainerName() != null) {
            container.setContainerName(updateDto.getContainerName());
            container.getGroup().setGroupName(updateDto.getContainerName());
        }
        
        if (updateDto.getContainerContent() != null) {
            container.setContainerContent(updateDto.getContainerContent());
        }
        
        if (updateDto.getVisibility() != null) {
            container.setVisibility(updateDto.getVisibility());
        }
        
        int memberCount = groupMemberRepository.findByGroup(container.getGroup()).size();
        return ContainerResponseDto.from(container, Authority.ROOT.name(), memberCount);
    }
    
    @Override
    @Transactional
    public void deleteContainer(Long containerId, String memberId) {
        Container container = containerRepository.findByContainerIdAndOwner(containerId,
                memberRepository.findById(memberId)
                        .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다.")))
                .orElseThrow(() -> new IllegalArgumentException("삭제 권한이 없습니다."));
        
        containerRepository.delete(container);
    }
    
    @Override
    @Transactional
    public GroupMemberResponseDto inviteMember(Long containerId, String requesterId, MemberInviteDto inviteDto) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new IllegalArgumentException("컨테이너를 찾을 수 없습니다."));
        
        Member requester = memberRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("요청자를 찾을 수 없습니다."));
        
        GroupMember requesterMember = groupMemberRepository.findByGroupAndMember(container.getGroup(), requester)
                .orElseThrow(() -> new IllegalArgumentException("초대 권한이 없습니다."));
        
        if (requesterMember.getAuthority() != Authority.ROOT) {
            throw new IllegalArgumentException("ROOT 권한이 필요합니다.");
        }
        
        Member invitedMember = memberRepository.findById(inviteDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("초대할 회원을 찾을 수 없습니다."));
        
        if (groupMemberRepository.existsByGroupAndMember(container.getGroup(), invitedMember)) {
            throw new IllegalArgumentException("이미 그룹에 속한 회원입니다.");
        }
        
        GroupMember newGroupMember = GroupMember.builder()
                .group(container.getGroup())
                .member(invitedMember)
                .authority(inviteDto.getAuthority())
                .build();
        
        groupMemberRepository.save(newGroupMember);
        
        return GroupMemberResponseDto.from(newGroupMember);
    }
    
    @Override
    public List<GroupMemberResponseDto> getContainerMembers(Long containerId, String memberId) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new IllegalArgumentException("컨테이너를 찾을 수 없습니다."));
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        
        if (container.getVisibility() == ContainerVisibility.PRIVATE &&
                !groupMemberRepository.existsByGroupAndMember(container.getGroup(), member)) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
        
        return groupMemberRepository.findByGroup(container.getGroup()).stream()
                .map(GroupMemberResponseDto::from)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void updateMemberAuthority(Long containerId, String requesterId, String targetMemberId, Authority newAuthority) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new IllegalArgumentException("컨테이너를 찾을 수 없습니다."));
        
        Member requester = memberRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("요청자를 찾을 수 없습니다."));
        
        GroupMember requesterMember = groupMemberRepository.findByGroupAndMember(container.getGroup(), requester)
                .orElseThrow(() -> new IllegalArgumentException("권한 변경 권한이 없습니다."));
        
        if (requesterMember.getAuthority() != Authority.ROOT) {
            throw new IllegalArgumentException("ROOT 권한이 필요합니다.");
        }
        
        Member targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new IllegalArgumentException("대상 회원을 찾을 수 없습니다."));
        
        GroupMember targetGroupMember = groupMemberRepository.findByGroupAndMember(container.getGroup(), targetMember)
                .orElseThrow(() -> new IllegalArgumentException("그룹에 속하지 않은 회원입니다."));
        
        if (container.getOwner().getMemberId().equals(targetMemberId)) {
            throw new IllegalArgumentException("컨테이너 소유자의 권한은 변경할 수 없습니다.");
        }
        
        targetGroupMember.setAuthority(newAuthority);
    }
    
    @Override
    @Transactional
    public void removeMember(Long containerId, String requesterId, String targetMemberId) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new IllegalArgumentException("컨테이너를 찾을 수 없습니다."));
        
        Member requester = memberRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("요청자를 찾을 수 없습니다."));
        
        GroupMember requesterMember = groupMemberRepository.findByGroupAndMember(container.getGroup(), requester)
                .orElseThrow(() -> new IllegalArgumentException("멤버 제거 권한이 없습니다."));
        
        if (requesterMember.getAuthority() != Authority.ROOT) {
            throw new IllegalArgumentException("ROOT 권한이 필요합니다.");
        }
        
        if (container.getOwner().getMemberId().equals(targetMemberId)) {
            throw new IllegalArgumentException("컨테이너 소유자는 제거할 수 없습니다.");
        }
        
        Member targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new IllegalArgumentException("대상 회원을 찾을 수 없습니다."));
        
        groupMemberRepository.deleteByGroupAndMember(container.getGroup(), targetMember);
    }
    
    @Override
    @Transactional
    public void leaveContainer(Long containerId, String memberId) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new IllegalArgumentException("컨테이너를 찾을 수 없습니다."));
        
        if (container.getOwner().getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("컨테이너 소유자는 탈퇴할 수 없습니다.");
        }
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        
        groupMemberRepository.deleteByGroupAndMember(container.getGroup(), member);
    }
    
    @Override
    @Transactional
    public void updateMemberActivity(Long containerId, String memberId) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new IllegalArgumentException("컨테이너를 찾을 수 없습니다."));
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        
        GroupMember groupMember = groupMemberRepository.findByGroupAndMember(container.getGroup(), member)
                .orElseThrow(() -> new IllegalArgumentException("그룹에 속하지 않은 회원입니다."));
        
        groupMember.setLastActivityDate(LocalDateTime.now());
    }
    
    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void removeInactiveMembers() {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        List<GroupMember> inactiveMembers = groupMemberRepository.findInactiveMembers(sixMonthsAgo);
        
        for (GroupMember member : inactiveMembers) {
            if (member.getAuthority() != Authority.ROOT) {
                groupMemberRepository.delete(member);
                log.info("6개월 이상 미활동 멤버 자동 삭제: {}", member.getMember().getMemberId());
            }
        }
    }
}