package solid.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "team_user")
public class TeamUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_user_id")
    private Integer teamUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_auth_id")
    private Auth teamAuth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    
    @Column(name = "joined_date", nullable = false)
    private LocalDateTime joinedDate = LocalDateTime.now();
    
    @Column(name = "last_activity_date")
    private LocalDateTime lastActivityDate;
    
    @Column(name = "is_online", nullable = false)
    private boolean isOnline = false;

}