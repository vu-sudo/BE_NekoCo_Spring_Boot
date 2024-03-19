package vjames.developer.MessConnect.Models.Dtos;

import jakarta.persistence.Id;
import lombok.*;
import vjames.developer.MessConnect.Models.Role;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    @Id
    private String id;
    private String email;
    private String username;
    private String appUserName;
    private String firstname;
    private String lastname;
    private String avatar;
    private String status;
    private Role role;
}
