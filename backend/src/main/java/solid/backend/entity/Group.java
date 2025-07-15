package solid.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 그룹 엔티티
 * 컨테이너에 속한 멤버들을 관리하기 위한 그룹
 * 한 컨테이너는 하나의 그룹을 가짐 (1:1 관계)
 */
@Entity
@Table(name = "그룹")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Group {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GROUP_ID")
    private Long groupId; // 그룹 고유 ID
    
    @Column(name = "GROUP_NAME", nullable = false, length = 100)
    private String groupName; // 그룹 이름 (주로 컨테이너 이름과 동일)
    
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GroupMember> groupMembers = new ArrayList<>(); // 그룹에 속한 멤버 목록
    
    @OneToOne(mappedBy = "group")
    private Container container; // 그룹과 연결된 컨테이너
}