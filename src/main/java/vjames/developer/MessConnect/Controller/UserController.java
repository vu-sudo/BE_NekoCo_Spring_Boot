package vjames.developer.MessConnect.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vjames.developer.MessConnect.Entities.User;
import vjames.developer.MessConnect.Models.Dtos.UserDto;
import vjames.developer.MessConnect.Services.UserDetailServiceImpl;
import vjames.developer.MessConnect.Utils.ApplicationResponseData;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserDetailServiceImpl userDetailService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/user/online")
    public User changeToOnline(@Payload User user) {
       try {
           messagingTemplate.convertAndSendToUser(user.getId(), "/topic", "Online on MeowCo");
           userDetailService.onConnect(user.getId());
           return user;
       } catch (Exception ex) {
           System.out.println(ex.getMessage());
           return null;
       }
    }

    @MessageMapping("/user/disconnect")
    public User changeToDisconnect(@Payload User user) {
        messagingTemplate.convertAndSendToUser(user.getId(),"/topic","Disconnected!");
        userDetailService.disconnect(user.getId());
        return user;
    }
    //todo update this function!
    @GetMapping("/allFollowingUsers")
    public ResponseEntity<?> findAllFollowingUser (@RequestParam("from") String from) {
        try {
            List<UserDto> users = userDetailService.findAllOnlineUser(from);
            return ApplicationResponseData.buildResponse(HttpStatus.OK, "Success", users);
        } catch (Exception ex) {
            return ApplicationResponseData.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");
        }
    }
}
