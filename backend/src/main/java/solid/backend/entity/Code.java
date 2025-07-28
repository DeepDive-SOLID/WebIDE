package solid.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.attoparser.dom.Text;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "code")
public class Code {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_id")
    @Comment("코드 ID")
    private Integer codeId;

    @Column(name = "code_name",length = 20, nullable = false)
    @Comment("파일명")
    private String codeName;

    @Column(name = "code_text")
    @Comment("코드 내용")
    private Text codeText;

    @Column(name = "code_upload_dt", nullable = false)
    @Comment("파일 업로드일")
    private LocalDate codeUploadDt;

    @Column(name = "code_create_dt", nullable = false)
    @Comment("파일 생성일")
    private LocalDate codeCreateDt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "directory_id")
    private Directory directory;


}
