package vjames.developer.MessConnect.Services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vjames.developer.MessConnect.Entities.Tokens;
import vjames.developer.MessConnect.Models.Role;
import vjames.developer.MessConnect.Entities.User;
import vjames.developer.MessConnect.Entities.Verify;
import vjames.developer.MessConnect.Exceptions.PasswordExpiredException;
import vjames.developer.MessConnect.JWT.JwtService;
import vjames.developer.MessConnect.Models.Dtos.UserDto;
import vjames.developer.MessConnect.Models.Mapper.UserDtoMapping;
import vjames.developer.MessConnect.Repositories.TokensRepository;
import vjames.developer.MessConnect.Repositories.UserRepository;
import vjames.developer.MessConnect.Repositories.VerifyRepository;
import vjames.developer.MessConnect.RequestBodies.LoginReqBody;
import vjames.developer.MessConnect.RequestBodies.RefreshTokenReqBody;
import vjames.developer.MessConnect.RequestBodies.RegisterReqBody;
import vjames.developer.MessConnect.CustomResponses.LoginResponse;
import vjames.developer.MessConnect.CustomResponses.RefreshAccessTokenResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokensRepository tokensRepository;
    private final VerifyRepository verifyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final int ACCESSTOKEN_EXPIRATION_TIME = 7;
    private final int REFRESHTOKEN_EXPIRATION_TIME = 14;

    public AuthenticationService(UserRepository userRepository, TokensRepository tokensRepository, VerifyRepository verifyRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.tokensRepository = tokensRepository;
        this.verifyRepository = verifyRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }
    private String extractEmailName(String email) {
        if(!email.contains("@") || !email.contains(".")) {
            return null;
        }
        String[] emailsParts = email.split("@");
        return emailsParts[0];
    }
    public UserDto createNewUser(RegisterReqBody request) {
        String defaultAvatarUrl = "https://res.cloudinary.com/dy1uuo6ql/image/upload/v1708924368/ipghhsijubgdawyxo99v" +
                ".jpg";
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getEmail())
                .appUserName(extractEmailName(request.getEmail()))
                .avatar(defaultAvatarUrl)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getUserRole()))
                .build();
        userRepository.save(user);
        UserDto userDto = UserDtoMapping.toUserDto(userRepository.findByUsername(request.getEmail()).orElseThrow());
        Verify verify = Verify.builder()
                .userId(userDto.getId())
                .userEmail(userDto.getEmail())
                .verifyToken(UUID.randomUUID().toString())
                .isVerify(false)
                .expireAt(LocalDateTime.now().plusMinutes(10))
                .build();
        verifyRepository.save(verify);
        return UserDtoMapping.toUserDto(user);
    }
    public UserDto findingUserByUsername(RegisterReqBody request) {
        try {
            return UserDtoMapping.toUserDto(userRepository.findByUsername(request.getEmail()).orElseThrow());
        } catch (Exception ex) {
            System.out.println(LocalDateTime.now() + "  User with email: " + request.getEmail() + " doesn't in " +
                    "database yet! Creating new user ->>");
        }
        return null;
    }
    public UserDto findingUserByUsername(String username) {
        try {
            return UserDtoMapping.toUserDto(userRepository.findByUsername(username).orElseThrow());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println(LocalDateTime.now() + "  User with email: " + username + " doesn't in " +
                    "database yet! Please try create");
        }
        return null;
    }
    public LoginResponse authenticate(LoginReqBody request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            User user = userRepository
                    .findByUsername(request.getUsername())
                    .orElseThrow(
                            () -> new UsernameNotFoundException(
                                    "User not found in database"));
            if (user.isCredentialsNonExpired()) {
                String accessToken = jwtService.generateToken(user);
                String refreshToken = UUID.randomUUID().toString();
                List<Tokens> tokens = tokensRepository.findAllByUserId(user.getId());
                int MAX_COUNT = 4;
                int numberOfTokens = tokens.size();
                if(numberOfTokens >= MAX_COUNT) {
                    Tokens tokenToDelete = tokensRepository.findEarliestExpiredTokenByUserId(user.getId());
                    tokensRepository.deleteByAccessToken(tokenToDelete.getAccessToken());
                    System.out.println(LocalDateTime.now() + "  Deleted tokenId = " + tokenToDelete.getId() +
                            " with " +
                            "expiration " +
                            "time is: " + tokenToDelete.getExpirationTime());
                }
                tokensRepository.save(Tokens.builder()
                        .userId(user.getId())
                        .tokenType("Bearer")
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .expirationTime(LocalDateTime.now().plusDays(ACCESSTOKEN_EXPIRATION_TIME))
                        .refreshTokenExpirationTime(LocalDateTime.now().plusDays(REFRESHTOKEN_EXPIRATION_TIME))
                        .isAccessTokenExpired(false)
                        .isRefreshTokenExpired(false)
                        .build());
                return LoginResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .appUsername(user.getAppUserName())
                        .tokenType("Bearer")
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            } else throw new PasswordExpiredException("User password has expired, Please reset your password");

        } catch (AuthenticationException exception) {
            throw new BadCredentialsException(exception.getMessage());
        }
    }
    public RefreshAccessTokenResponse refreshAccessToken(RefreshTokenReqBody request) {
        Tokens token = tokensRepository.findByRefreshToken(request.getRefreshToken());

        try {
            if(token !=null && !token.getIsRefreshTokenExpired() && !token.getRefreshTokenExpirationTime().isBefore(LocalDateTime.now())) {
                UserDto userDto = UserDtoMapping.toUserDto(userRepository.findById(token.getUserId()).orElseThrow());
                String newAccessToken = jwtService.generateToken(userDto);
                String newRefreshToken = UUID.randomUUID().toString();

                tokensRepository.updateTokensById(newAccessToken, newRefreshToken,LocalDateTime.now().plusDays(ACCESSTOKEN_EXPIRATION_TIME),
                        LocalDateTime.now().plusDays(REFRESHTOKEN_EXPIRATION_TIME),
                        token.getId());
                return RefreshAccessTokenResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(newRefreshToken)
                        .build();
            } else {
                System.out.println(LocalDateTime.now() + "  Token is not valid or expired!");
            }
        }catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
    public boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
    public boolean isValidEmail(String email) {
        String emailPattern =   "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return  matcher.matches();
    }
}
