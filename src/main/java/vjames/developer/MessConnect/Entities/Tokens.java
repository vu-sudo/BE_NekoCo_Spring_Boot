package vjames.developer.MessConnect.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tokens")
public class Tokens {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "token_type")
    private String tokenType;
    @Column(name = "access_token")
    private String accessToken;
    @Column(name = "refresh_token")
    private String refreshToken;
    @Column(name = "is_access_token_expired")
    private Boolean isAccessTokenExpired;
    @Column(name = "is_refresh_token_expired")
    private Boolean isRefreshTokenExpired;
    @Column(name = "refresh_token_expiration_time")
    private LocalDateTime refreshTokenExpirationTime;
    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;
}
