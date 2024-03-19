package vjames.developer.MessConnect.RequestBodies;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class ResetPasswordReqBody {
    private String username;
    private String newPassword;
}
