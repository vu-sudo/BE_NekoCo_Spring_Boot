package vjames.developer.MessConnect.RequestBodies;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RefreshTokenReqBody {
    private String refreshToken;
}
