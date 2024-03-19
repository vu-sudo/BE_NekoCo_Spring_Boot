package vjames.developer.MessConnect.CustomResponses;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshAccessTokenResponse {
    private String accessToken;
    private String refreshToken;
}
