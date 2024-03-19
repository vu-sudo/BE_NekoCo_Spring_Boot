package vjames.developer.MessConnect.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vjames.developer.MessConnect.Entities.Tokens;

import java.time.LocalDateTime;
import java.util.List;

public interface TokensRepository extends JpaRepository<Tokens, String> {
    @Modifying
    @Query("SELECT t FROM Tokens t WHERE t.expirationTime < :currentDateTime OR t.refreshTokenExpirationTime < :currentDateTime")
    List<Tokens> findExpiredTokens(@Param("currentDateTime") LocalDateTime currentDateTime);

    Tokens findByRefreshToken(String refreshToken);
    Tokens findByAccessToken(String accessToken);

    @Modifying
    @Transactional
    @Query("UPDATE Tokens t SET t.accessToken = :newAccessToken, t.refreshToken = :newRefreshToken, t.expirationTime " +
            "= :newExpirationTime, t.refreshTokenExpirationTime = :newRefreshTokenExpiration, t.isAccessTokenExpired " +
            "= false , t.isRefreshTokenExpired = false" +
            " WHERE t.id = " +
            ":tokenId")
    void updateTokensById(@Param("newAccessToken") String newAccessToken,
                          @Param("newRefreshToken") String newRefreshToken,
                          @Param("newExpirationTime") LocalDateTime newExpirationTime,
                          @Param("newRefreshTokenExpiration") LocalDateTime newRefreshTokenExpiration,
                          @Param("tokenId") String tokenId);
    @Modifying
    @Transactional
    void deleteByAccessToken(String token);

    @Transactional
    List<Tokens> findAllByUserId(String userId);

    @Transactional
    @Query("SELECT T FROM Tokens T WHERE T.userId = :userId ORDER BY T.expirationTime LIMIT 1")
    Tokens findEarliestExpiredTokenByUserId(@Param("userId") String userId);
}
