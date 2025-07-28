package solid.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    @Comment("그룹 ID")
    private Integer teamId;

    @Column(name = "team_name")
    @Comment("그룹 이름")
    private String teamName;

    @OneToMany(mappedBy = "team")
    @JsonIgnore
    private List<Container> containers = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TeamUser> teamUsers = new ArrayList<>();
}
