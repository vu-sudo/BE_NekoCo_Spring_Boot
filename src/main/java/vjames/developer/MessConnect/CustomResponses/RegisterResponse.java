package vjames.developer.MessConnect.CustomResponses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class RegisterResponse {
    private String userId;
    private String username;

    public RegisterResponse(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }
}
