package vjames.developer.MessConnect.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import vjames.developer.MessConnect.Entities.User;
import vjames.developer.MessConnect.Models.Dtos.UserDto;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public <T> T extractClaims(String token, Function<Claims, T> resolvers) {
        Claims claims = extractAllClaims(token);
        return resolvers.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token
                ).getPayload();
    }

    public boolean isValid(String token, UserDetails user) {
        String username = extractUsername(token);
        return username.equals(user.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    private SecretKey getSigninKey() {
        String SECRET_KEY = "92f1b1366cec70d2f7cbb93f8f1ef4a42034b9b9e1233cafb026bc60cbed31c7";
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claims(
                        Map.of(
                                "username", user.getEmail(),
                                "role", user.getRole())
                )
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 7*24*60*60*1000))
                .signWith(getSigninKey())
                .compact();
    }
    public String generateToken(UserDto user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claims(
                        Map.of(
                                "username", user.getEmail(),
                                "role", user.getRole())
                )
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 7*24*60*60*1000))
                .signWith(getSigninKey())
                .compact();
    }

}
