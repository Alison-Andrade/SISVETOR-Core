package br.gov.endemias.config.security;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.gov.endemias.domain.entity.User;


@Component
public class TokenConfig {
    
    @Value("${JWT_SECRET:secret}")
    private String jwtSecret;

    public String generateToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);

        return JWT.create()
                .withClaim("userId", user.getId())
                .withClaim("role", user.getRole())
                .withSubject(user.getAgente().getCpf())
                .withExpiresAt(Instant.now().plusSeconds(86400))
                .withIssuedAt(Instant.now())
                .sign(algorithm);
    }

    public Optional<JWTUserData> validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);

            DecodedJWT decoded = JWT.require(algorithm)
                    .build()
                    .verify(token);

            return Optional.of(JWTUserData.builder()
                    .userId(decoded.getClaim("userId").asLong())
                    .cpf(decoded.getSubject())
                    .role(decoded.getClaim("role").asString())
                    .build());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    

}
