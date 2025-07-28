package solid.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Comment;

@Data
@Entity
@Table(name = "auth")
public class Auth {

    @Id
    @Column(name = "auth_id", length = 10)
    @Comment("권한 코드")
    private String authId;

    @Column(name = "auth_name", length = 20, nullable = false)
    @Comment("권한 이름")
    private String authName;
}
