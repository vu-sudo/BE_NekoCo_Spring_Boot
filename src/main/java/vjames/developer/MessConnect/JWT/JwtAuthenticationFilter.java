package vjames.developer.MessConnect.JWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vjames.developer.MessConnect.Entities.Tokens;
import vjames.developer.MessConnect.Repositories.TokensRepository;
import vjames.developer.MessConnect.Services.UserDetailServiceImpl;

import java.time.LocalDateTime;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final TokensRepository tokensRepository;
    private final UserDetailServiceImpl userDetailService;

    public JwtAuthenticationFilter(JwtService jwtService, TokensRepository tokensRepository, UserDetailServiceImpl userDetailService) {
        this.jwtService = jwtService;
        this.tokensRepository = tokensRepository;
        this.userDetailService = userDetailService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) {
        try {
            String authHeader = request.getHeader("Authorization");
            if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = authHeader.substring(7);
            Tokens isExistedAccessToken = tokensRepository.findByAccessToken(token);
            String username = jwtService.extractUsername(token);
            if(isExistedAccessToken == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                System.out.println(LocalDateTime.now() + "  Bearer token from request is invalid!");
            }
            if(username != null && isExistedAccessToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailService.loadUserByUsername(username);
                if(jwtService.isValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
                filterChain.doFilter(request, response);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
