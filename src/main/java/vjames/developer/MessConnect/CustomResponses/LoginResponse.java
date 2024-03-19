package vjames.developer.MessConnect.CustomResponses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class LoginResponse {
    private String id;
    private String email;
//    private String username;
    private String appUsername;
    private String tokenType;
    private String accessToken;
    private String refreshToken;

}
