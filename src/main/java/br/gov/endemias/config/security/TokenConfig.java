package br.gov.endemias.config.security;

import java.time.Instant;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.gov.endemias.domain.entity.User;


@Component
public class TokenConfig {
    public static final long ACCESS_TOKEN_SECONDS = Duration.ofMinutes(15).toSeconds();

    private final String jwtSecret;

    public TokenConfig(@Value("${JWT_SECRET}") String jwtSecret) {
        if (jwtSecret.isBlank() || jwtSecret.length() < 32) {
            throw new IllegalArgumentException("JWT_SECRET deve conter pelo menos 32 caracteres.");
        }
        this.jwtSecret = jwtSecret;
    }

    public String generateToken(User user, UUID sessionId) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        Instant now = Instant.now();

        return JWT.create()
                .withClaim("userId", user.getId())
                .withClaim("role", user.getRole().name())
                .withClaim("sid", sessionId.toString())
                .withSubject(user.getAgente().getCpf())
                .withExpiresAt(now.plusSeconds(ACCESS_TOKEN_SECONDS))
                .withIssuedAt(now)
                .sign(algorithm);
    }

    public Optional<JWTUserData> validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);

            DecodedJWT decoded = JWT.require(algorithm)
                    .build()
                    .verify(token);

            String sessionClaim = decoded.getClaim("sid").asString();

            return Optional.of(JWTUserData.builder()
                    .userId(decoded.getClaim("userId").asLong())
                    .cpf(decoded.getSubject())
                    .role(decoded.getClaim("role").asString())
                    .sessionId(sessionClaim == null ? null : UUID.fromString(sessionClaim))
                    .build());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    

}
