package vjames.developer.MessConnect.Services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vjames.developer.MessConnect.Entities.ResetPasswordTokens;
import vjames.developer.MessConnect.Entities.Tokens;
import vjames.developer.MessConnect.Models.Dtos.UserDto;
import vjames.developer.MessConnect.Models.Mapper.UserDtoMapping;
import vjames.developer.MessConnect.Repositories.ResetPasswordTokensRepository;
import vjames.developer.MessConnect.Repositories.TokensRepository;
import vjames.developer.MessConnect.Repositories.UserRepository;
import vjames.developer.MessConnect.RequestBodies.ResetPasswordReqBody;
import vjames.developer.MessConnect.Utils.RandomSequenceGenerator;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Service
public class TokenService {
    private final TokensRepository tokensRepository;
    private final ResetPasswordTokensRepository resetPasswordTokensRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public TokenService(TokensRepository tokensRepository, ResetPasswordTokensRepository resetPasswordTokensRepository, UserRepository userRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.tokensRepository = tokensRepository;
        this.resetPasswordTokensRepository = resetPasswordTokensRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }
    public boolean isValidRefreshToken(String refreshToken) {
        Tokens tokens = tokensRepository.findByRefreshToken(refreshToken);
        return  tokens != null && !tokens.getIsRefreshTokenExpired();
    }
    public void removeAccessToken(String accessToken) {
        tokensRepository.deleteByAccessToken(accessToken);
    }
    public void createAndSendResetPassCodeToUserWithId(String userId, String email) {
        String passCode = RandomSequenceGenerator.generateRandomSequenceWithText();
        ResetPasswordTokens resetPasswordTokens = resetPasswordTokensRepository.findByUserId(userId);
        if(resetPasswordTokens != null) {
            resetPasswordTokensRepository.deleteById(resetPasswordTokens.getId());
        }
        resetPasswordTokensRepository.save(
                ResetPasswordTokens.builder()
                        .userId(userId)
                        .resetCode(passCode)
                        .expireAt(LocalDateTime.now().plusMinutes(10))
                        .build()
        );
        emailService.sendEmailToResetPassword(email, Map.of(
                "passCode", passCode
        ));
    }
    public void getPassCodeAndUpdatingUserPassword(String passCode, ResetPasswordReqBody request) {
        ResetPasswordTokens resetPasswordTokens = resetPasswordTokensRepository.findByResetCode(passCode);
        UserDto userDto = UserDtoMapping.toUserDto(userRepository.findById(resetPasswordTokens.getUserId()).orElseThrow());
        if(userDto != null && Objects.equals(resetPasswordTokens.getUserId(), userDto.getId())) {
            userRepository.updatePassword(passwordEncoder.encode(request.getNewPassword()), userDto.getId());
            System.out.println("UPDATED USER WITH NEW PASSWORD!");
            resetPasswordTokensRepository.deleteById(resetPasswordTokens.getId());
            System.out.println("DELETE RESET PASSWORD TOKEN");
        }
    }
    public boolean isResetPasswordCodeExpired(String passCode) {
        ResetPasswordTokens resetPasswordTokens = resetPasswordTokensRepository.findByResetCode(passCode);
        return resetPasswordTokens.getExpireAt().isBefore(LocalDateTime.now());
    }
    public boolean isResetPasswordCodeExistedInDB(String passCode) {
        ResetPasswordTokens resetPasswordTokens = resetPasswordTokensRepository.findByResetCode(passCode);
        return resetPasswordTokens != null;
    }
}
