package br.gov.endemias.config.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import br.gov.endemias.domain.entity.Agente;
import br.gov.endemias.domain.entity.User;
import br.gov.endemias.domain.enums.UserRole;

class TokenConfigTest {
    private static final String SECRET = "abcdefghijklmnopqrstuvwxyz1234567890";

    @Test
    void novoJwtCarregaSessaoEExpiraEmQuinzeMinutos() {
        TokenConfig tokens = new TokenConfig(SECRET);
        User user = usuario();
        UUID sessionId = UUID.randomUUID();

        String token = tokens.generateToken(user, sessionId);

        assertEquals(sessionId, tokens.validateToken(token).orElseThrow().sessionId());
        long lifetime = JWT.decode(token).getExpiresAtAsInstant().getEpochSecond()
            - JWT.decode(token).getIssuedAtAsInstant().getEpochSecond();
        assertEquals(900L, lifetime);
    }

    @Test
    void jwtAnteriorSemSessaoAindaEValidoAteExpirar() {
        TokenConfig tokens = new TokenConfig(SECRET);
        String legacy = JWT.create()
            .withClaim("userId", 1L)
            .withClaim("role", "ROLE_CAMPO")
            .withSubject("12345678909")
            .withIssuedAt(Instant.now())
            .withExpiresAt(Instant.now().plusSeconds(3600))
            .sign(Algorithm.HMAC256(SECRET));

        assertNull(tokens.validateToken(legacy).orElseThrow().sessionId());
    }

    private User usuario() {
        Agente agente = new Agente();
        agente.setCpf("12345678909");
        User user = new User();
        user.setId(1L);
        user.setAgente(agente);
        user.setRole(UserRole.ROLE_CAMPO);
        return user;
    }
}
