package vjames.developer.MessConnect.Services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vjames.developer.MessConnect.Entities.User;
import vjames.developer.MessConnect.Models.Dtos.UserDto;
import vjames.developer.MessConnect.Models.Mapper.UserDtoMapping;
import vjames.developer.MessConnect.Models.UserStatus;
import vjames.developer.MessConnect.Repositories.UserRepository;

import java.util.List;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepository repository;

    public UserDetailServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not " +
                "found!"));
    }
    public void onConnect(String userId) {
        repository.findByIdOptional(userId).ifPresent(
                user -> repository.updateStatus(UserStatus.ONLINE.toString(), user.getUsername())
        );
        System.out.println(userId + " connected!");
    }
    public void disconnect(String userId) {
        repository.findByIdOptional(userId).ifPresent(
                user -> repository.updateStatus(UserStatus.OFFLINE.toString(), user.getUsername())
        );
        System.out.println(userId + " disconnected!");
    }
    public List<UserDto> findAllOnlineUser(String userId) {
        List<User> users = repository.findAllByStatus(userId,"ONLINE");
        return UserDtoMapping.toUserDtoList(users);
    }
}
