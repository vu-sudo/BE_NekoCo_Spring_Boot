package vjames.developer.MessConnect.CustomResponses;

import lombok.*;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserFollowingResponses {
    private String id;
    private String appUserName;
    private String firstName;
    private String lastName;
    private String avatar;
    private String followAt;
}
