package vjames.developer.MessConnect.RequestBodies;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class LoginReqBody {
    private String username;
    private String password;

    public LoginReqBody(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
