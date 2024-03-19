package vjames.developer.MessConnect.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "verified_accounts")
public class Verify {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "user_email")
    private String userEmail;
    @Column(name = "verify_token")
    private String verifyToken;
    @Column(name = "expire_at")
    private LocalDateTime expireAt;
    @Column(name = "is_verify")
    private Boolean isVerify;
    @Column(name = "verify_at")
    private LocalDateTime verifyAt;
}
