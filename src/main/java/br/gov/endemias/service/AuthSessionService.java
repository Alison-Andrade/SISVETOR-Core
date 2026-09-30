package br.gov.endemias.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import br.gov.endemias.config.security.TokenConfig;
import br.gov.endemias.domain.entity.AuthRefreshToken;
import br.gov.endemias.domain.entity.AuthSession;
import br.gov.endemias.domain.entity.User;
import br.gov.endemias.domain.enums.AuthClientType;
import br.gov.endemias.dto.AuthResponse;
import br.gov.endemias.repository.AuthRefreshTokenRepository;
import br.gov.endemias.repository.AuthSessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthSessionService {
    private static final Duration REFRESH_LIFETIME = Duration.ofDays(30);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final AuthSessionRepository sessions;
    private final AuthRefreshTokenRepository refreshTokens;
    private final TokenConfig tokenConfig;

    @Transactional
    public AuthResponse login(User user, AuthClientType clientType) {
        Instant now = Instant.now();
        AuthSession session = new AuthSession();
        session.setId(UUID.randomUUID());
        session.setUser(user);
        session.setClientType(clientType);
        session.setCreatedAt(now);
        session.setExpiresAt(now.plus(REFRESH_LIFETIME));
        sessions.save(session);
        return issueTokens(session, now);
    }

    @Transactional(dontRollbackOn = BadCredentialsException.class)
    public AuthResponse refresh(String rawToken, AuthClientType clientType) {
        UUID sessionId = parseSessionId(rawToken)
            .orElseThrow(() -> new BadCredentialsException("Refresh token inválido"));
        AuthSession session = sessions.findByIdForUpdate(sessionId)
            .orElseThrow(() -> new BadCredentialsException("Sessão inválida"));
        Instant now = Instant.now();
        if (session.getClientType() != clientType || !session.isActive(now) || !session.getUser().isEnabled()) {
            throw new BadCredentialsException("Sessão inválida");
        }
        AuthRefreshToken stored = refreshTokens.findById(hash(rawToken))
            .filter(token -> token.getSession().getId().equals(sessionId))
            .orElseThrow(() -> new BadCredentialsException("Refresh token inválido"));
        if (stored.getConsumedAt() != null) {
            session.setRevokedAt(now);
            throw new BadCredentialsException("Refresh token reutilizado");
        }
        stored.setConsumedAt(now);
        return issueTokens(session, now);
    }

    @Transactional
    public void logout(String rawToken, AuthClientType clientType) {
        parseSessionId(rawToken).ifPresent(id -> sessions.findByIdForUpdate(id).ifPresent(session -> {
            if (session.getClientType() == clientType && refreshTokens.findById(hash(rawToken))
                    .filter(token -> token.getSession().getId().equals(id)).isPresent()) {
                session.setRevokedAt(Instant.now());
            }
        }));
    }

    public boolean isActive(UUID sessionId, Long userId) {
        return sessions.existsByIdAndUserIdAndRevokedAtIsNullAndExpiresAtAfter(sessionId, userId, Instant.now());
    }

    private AuthResponse issueTokens(AuthSession session, Instant now) {
        byte[] random = new byte[32];
        RANDOM.nextBytes(random);
        String rawToken = session.getId() + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(random);
        AuthRefreshToken token = new AuthRefreshToken();
        token.setTokenHash(hash(rawToken));
        token.setSession(session);
        token.setCreatedAt(now);
        refreshTokens.save(token);
        return new AuthResponse(tokenConfig.generateToken(session.getUser(), session.getId()),
            "Bearer", rawToken, TokenConfig.ACCESS_TOKEN_SECONDS);
    }

    private Optional<UUID> parseSessionId(String rawToken) {
        if (rawToken == null || rawToken.length() > 100 || rawToken.length() < 80) {
            return Optional.empty();
        }
        int separator = rawToken.indexOf('.');
        if (separator != 36 || rawToken.length() != 80) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(rawToken.substring(0, separator)));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private String hash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 indisponível", ex);
        }
    }
}
