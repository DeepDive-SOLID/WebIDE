package solid.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * 컨테이너 엔티티
 * Web IDE에서 사용자가 생성하는 프로젝트 작업 공간
 * 각 컨테이너는 하나의 그룹을 가지며, 여러 멤버가 참여 가능
 */
@Entity
@Table(name = "container")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Container {
    
    /**
     * 컨테이너 고유 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "container_id")
    private Integer containerId;
    
    /**
     * 컨테이너가 속한 팀 (멤버 관리를 위한 그룹)
     */
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;
    
    /**
     * 컨테이너 공개 여부 (true: 공개, false: 비공개)
     */
    @Column(name = "container_auth", nullable = false)
    @Builder.Default
    private Boolean containerAuth = true;
    
    /**
     * 컨테이너 생성일 (기본값: 현재 날짜)
     */
    @Column(name = "container_date", nullable = false)
    @Builder.Default
    private LocalDate containerDate = LocalDate.now();
    
    /**
     * 컨테이너 이름 (최대 20자)
     */
    @Column(name = "container_nm", nullable = false, length = 20)
    private String containerName;
    
    /**
     * 컨테이너 설명 (최대 200자)
     */
    @Column(name = "container_content", length = 200)
    private String containerContent;
}