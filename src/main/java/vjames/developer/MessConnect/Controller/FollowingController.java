package vjames.developer.MessConnect.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vjames.developer.MessConnect.CustomResponses.UserFollowingResponses;
import vjames.developer.MessConnect.Entities.Follows;
import vjames.developer.MessConnect.Models.Dtos.UserDto;
import vjames.developer.MessConnect.Services.FollowingService;
import vjames.developer.MessConnect.Utils.ApplicationResponseData;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/follows")
@RequiredArgsConstructor
public class FollowingController {
    private final FollowingService followingService;

    @GetMapping("/suggest")
    public ResponseEntity<Map<String, Object>> suggestUserToFollowing(@RequestParam("fromUser") String fromUser) {
        try {
            List<UserDto> userDtos = followingService.getAllSuggestUser(fromUser);
            return ApplicationResponseData.buildResponse(HttpStatus.OK, "", userDtos);
        } catch (Exception ex) {
            return ApplicationResponseData.responseInternalError();
        }
    }

    @PostMapping("/followToUser")
    public ResponseEntity<Map<String, Object>> followUser(
            @RequestParam("fromUser") String fromUser,
            @RequestParam("toUser") String toUser
    ) {
        try {
            if (fromUser.equals(toUser)) {
                return ApplicationResponseData.buildResponse(HttpStatus.BAD_REQUEST, "Cant follow your self");
            }
            Follows follows = followingService.findFollowWithCurrenUserIdAndFollowedId(fromUser, toUser);
            if (follows != null) {
                return ApplicationResponseData.buildResponse(HttpStatus.FOUND, "Had been follow before!");
            }
            followingService.followUser(fromUser, toUser);
            return ApplicationResponseData.buildResponse(HttpStatus.OK, "Follow success!");
        } catch (Exception ex) {
            return ApplicationResponseData.responseInternalError();
        }
    }

    @DeleteMapping("/unfollowFromUser")
    public ResponseEntity<Map<String, Object>> unfollowUser(
            @RequestParam("fromUser") String fromUser,
            @RequestParam("toUser") String toUser
    ) {
        try {
            if (fromUser.equals(toUser)) {
                return ApplicationResponseData.buildResponse(HttpStatus.BAD_REQUEST, "Something went wrong when do unfollowing");
            }
            followingService.unfollowUser(fromUser, toUser);
            return ApplicationResponseData.buildResponse(HttpStatus.OK, "Unfollow success!");
        } catch (Exception ex) {
            return ApplicationResponseData.responseInternalError();
        }
    }

    @GetMapping("/allFollowingOfUser")
    public ResponseEntity<Map<String, Object>> allFollowingUser(
            @RequestParam("fromUser") String fromUser
    ) {
        try {
            List<UserFollowingResponses> userFollowingResponses = followingService.findAllUserThatYouAreFollowing(fromUser);
            return ApplicationResponseData.buildResponse(HttpStatus.OK, "", userFollowingResponses);
        } catch (Exception ex) {
            return ApplicationResponseData.responseInternalError();
        }
    }

    @GetMapping("/allFollowsUser")
    public ResponseEntity<Map<String, Object>> allFollowUser(
            @RequestParam("fromUser") String fromUser
    ) {
        try {
            List<UserFollowingResponses> userFollowingResponses = followingService.findAllUserThatFollowYou(fromUser);
            return ApplicationResponseData.buildResponse(HttpStatus.OK, "", userFollowingResponses);
        } catch (Exception ex) {
            return ApplicationResponseData.responseInternalError();
        }
    }

}
