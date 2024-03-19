package vjames.developer.MessConnect.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reset_user_password_token")
public class ResetPasswordTokens {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "reset_code")
    private String resetCode;
    @Column(name = "expire_at")
    private LocalDateTime expireAt;

}
