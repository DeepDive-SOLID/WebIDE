package solid.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "directory")
public class Directory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "directory_id")
    @Comment("디렉토리 ID")
    private Integer directoryId;

    @Column(name = "directory_name", length = 30, nullable = false)
    @Comment("디렉토리 이름")
    private String directoryName;

    @Column(name = "directory_root", length = 30, nullable = false)
    @Comment("상위 디렉토리")
    private String directoryRoot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "container_id")
    private Container container;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_directory_id")
    @Comment("부모 디렉토리")
    private Directory parentDirectory;

    @OneToMany(mappedBy = "directory", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private List<CodeFile> codeFiles = new ArrayList<>();

    @OneToMany(mappedBy = "directory", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private List<Code> codes = new ArrayList<>();

    @OneToMany(mappedBy = "directory", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private List<Progress> progresses = new ArrayList<>();

    @OneToMany(mappedBy = "directory", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    @Comment("연결된 문제들")
    private List<Question> questions = new ArrayList<>();
    
    @OneToMany(mappedBy = "parentDirectory", cascade = CascadeType.ALL)
    @JsonIgnore
    @Comment("하위 디렉토리들")
    private List<Directory> subDirectories = new ArrayList<>();
}
