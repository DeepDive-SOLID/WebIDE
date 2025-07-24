package solid.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "container")
public class Container {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "container_id")
    @Comment("방 번호")
    private Integer containerId;

    @Column(name = "container_auth", nullable = false)
    @Comment("true: public, false: private")
    private Boolean containerAuth;

    @Column(name = "container_date", nullable = false)
    @Comment("생성일")
    private LocalDate containerDate;

    @Column(name = "container_nm", length = 20, nullable = false)
    @Comment("컨테이너 이름")
    private String containerNm;

    @Column(name = "container_content",length = 200)
    @Comment("컨테이너 설명")
    private String containerContent;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "container", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Directory> directory = new ArrayList<>();
}
