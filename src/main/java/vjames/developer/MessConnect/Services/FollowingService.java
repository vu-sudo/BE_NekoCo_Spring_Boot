package vjames.developer.MessConnect.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vjames.developer.MessConnect.CustomResponses.UserFollowingResponses;
import vjames.developer.MessConnect.Entities.Follows;
import vjames.developer.MessConnect.Entities.User;
import vjames.developer.MessConnect.Models.Dtos.UserDto;
import vjames.developer.MessConnect.Models.Mapper.UserDtoMapping;
import vjames.developer.MessConnect.Repositories.FollowingRepository;
import vjames.developer.MessConnect.Repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowingService {
    private final UserRepository userRepository;
    private final FollowingRepository followingRepository;

    public List<UserDto> getAllSuggestUser(String userId) {
        List<User> users = userRepository.findAllIfNotFollowingByCurrenUserId(userId);
        return UserDtoMapping.toUserDtoList(users);
    }

    public void followUser(String fromUser, String toUser) {
        followingRepository.save(Follows.builder()
                .userId(fromUser)
                .followingId(toUser)
                .followAt(LocalDateTime.now())
                .build()
        );
    }

    public Follows findFollowWithCurrenUserIdAndFollowedId (String fromUser, String toUser) {
        return followingRepository.findByUserIdAndFollowingId(fromUser, toUser);
    }
    public void unfollowUser(String fromUser, String toUser) {
        Follows follow = findFollowWithCurrenUserIdAndFollowedId(fromUser, toUser);
        if(follow != null) {
            followingRepository.deleteById(follow.getId());
        }
    }
    private List<UserFollowingResponses> getUserFollowingResponses(List<Object[]> objects) {
        List<UserFollowingResponses> userFollowingResponses = new ArrayList<>();
        for(Object[] result: objects) {
            UserFollowingResponses userFollowingResponsesItem = new UserFollowingResponses();
            userFollowingResponsesItem.setId((String) result[0]);
            userFollowingResponsesItem.setAppUserName((String) result[1]);
            userFollowingResponsesItem.setAvatar((String) result[2]);
            userFollowingResponsesItem.setFirstName((String) result[3]);
            userFollowingResponsesItem.setLastName((String) result[4]);
            userFollowingResponsesItem.setFollowAt(result[5].toString());
            userFollowingResponses.add(userFollowingResponsesItem);
        }
        return userFollowingResponses;
    }
    public List<UserFollowingResponses> findAllUserThatYouAreFollowing (String getAllFromUser) {
        List<Object[]> objects = followingRepository.findAllUserFollowingFromUserId(getAllFromUser);
        return getUserFollowingResponses(objects);
    }

    public List<UserFollowingResponses> findAllUserThatFollowYou (String getAllFromUser) {
        List<Object[]> objects = followingRepository.findAllFollowsToUser(getAllFromUser);
        return getUserFollowingResponses(objects);
    }

}
