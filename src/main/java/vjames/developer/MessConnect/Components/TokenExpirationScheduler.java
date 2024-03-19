package vjames.developer.MessConnect.Components;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vjames.developer.MessConnect.Entities.Tokens;
import vjames.developer.MessConnect.Repositories.TokensRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
public class TokenExpirationScheduler {
    private final TokensRepository tokensRepository;

    public TokenExpirationScheduler(TokensRepository tokensRepository) {
        this.tokensRepository = tokensRepository;
    }
    @Scheduled(fixedRate = 60_000 * 3)
    public void updateTokenEntityStatus() {
        try {
            List<Tokens> tokens = tokensRepository.findExpiredTokens(LocalDateTime.now());
            for(Tokens token: tokens) {
                token.setIsAccessTokenExpired(token.getExpirationTime().isBefore(LocalDateTime.now()));
                token.setIsRefreshTokenExpired(token.getRefreshTokenExpirationTime().isBefore(LocalDateTime.now()));
                tokensRepository.save(token);
            }
            System.out.println(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")) + "  Updating token scheduler was " +
                    "worked!");
        } catch (RuntimeException ex) {
            throw new RuntimeException("Scheduler run with an error!");
        }
    }
}
