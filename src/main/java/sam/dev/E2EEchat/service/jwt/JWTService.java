package sam.dev.E2EEchat.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sam.dev.E2EEchat.repository.entitys.user.User;
import sam.dev.E2EEchat.repository.user.UserRepository;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JWTService {

    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);

    private final UserRepository userRepository;

    @Value("${security.jwt.secret-key}")
    private String secret;

    @Value("${security.jwt.expiration-time}")
    private int expiration;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(Long userId) {
        try {
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) return Jwts
                    .builder()
                    .subject(optionalUser.get().getUsername())
                    .claim("userId", userId)
                    .issuedAt(new Date())
                    .expiration(new Date((new Date().getTime() + expiration)))
                    .signWith(getSecretKey())
                    .compact();

            return "user wasn't found";

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return "something went wrong";
    }

    private Claims getAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long extractUsersId(String token) {
        return getAllClaims(token).get("userId", Long.class);
    }

    public String extractUsername(String token) {
        return getAllClaims(token).getSubject();
    }

    public boolean validateToken(
            String token,
            UserDetails userDetails
    ) {
        try {
            Optional<User> optionalUser = userRepository.findById(extractUsersId(token));
            if (optionalUser.isEmpty()) return false;
            final Date expiration = getAllClaims(token).getExpiration();
            boolean isExpired = expiration.after(new Date());

            return (optionalUser.get().getUsername().equals(userDetails.getUsername()) && isExpired);

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return false;
    }
}
