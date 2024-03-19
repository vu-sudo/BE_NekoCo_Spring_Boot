package vjames.developer.MessConnect.RequestBodies;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class RegisterReqBody {
    private String email;
    private String password;
    private String userRole;
}
