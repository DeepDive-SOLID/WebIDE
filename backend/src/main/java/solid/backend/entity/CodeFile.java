package solid.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "code_file")
public class CodeFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_file_id")
    @Comment("파일 ID")
    private Integer codeFileId;

    @Column(name = "code_file_path", length = 100, nullable = false)
    @Comment("파일 경로")
    private String codeFilePath;

    @Column(name = "code_file_name",length = 20, nullable = false)
    @Comment("파일 이름")
    private String codeFileName;

    @Column(name = "code_file_upload_dt", nullable = false)
    @Comment("파일 업로드일")
    private LocalDate codeFileUploadDt;

    @Column(name = "code_file_create_dt", nullable = false)
    @Comment("파일 생성일")
    private LocalDate codeFileCreateDt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "directory_id")
    private Directory directory;

}
