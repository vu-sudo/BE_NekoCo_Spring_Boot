package vjames.developer.MessConnect.Models.Mapper;

import vjames.developer.MessConnect.Entities.User;
import vjames.developer.MessConnect.Models.Dtos.UserDto;

import java.util.ArrayList;
import java.util.List;

public class UserDtoMapping {
    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstname(user.getFirstName())
                .lastname(user.getLastName())
                .username(user.getUsername())
                .appUserName(user.getAppUserName())
                .avatar(user.getAvatar())
                .status(user.getStatus())
                .role(user.getRole())
                .build();
    }
    public static List<UserDto> toUserDtoList(List<User> users) {
        List<UserDto> userDtos = new ArrayList<>();
        for(User user: users) {
            userDtos.add(toUserDto(user));
        }
        return userDtos;
    }
}
