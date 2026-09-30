package br.gov.endemias.service;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.verify;
import org.springframework.security.authentication.BadCredentialsException;

import br.gov.endemias.config.security.TokenConfig;
import br.gov.endemias.domain.entity.Agente;
import br.gov.endemias.domain.entity.AuthRefreshToken;
import br.gov.endemias.domain.entity.AuthSession;
import br.gov.endemias.domain.entity.User;
import br.gov.endemias.domain.enums.AuthClientType;
import br.gov.endemias.domain.enums.UserRole;
import br.gov.endemias.domain.enums.UserStatus;
import br.gov.endemias.dto.AuthResponse;
import br.gov.endemias.repository.AuthRefreshTokenRepository;
import br.gov.endemias.repository.AuthSessionRepository;

class AuthSessionServiceTest {
    @Test
    void rotacionaRefreshERevogaSessaoNoReplay() throws Exception {
        AuthSessionRepository sessions = mock(AuthSessionRepository.class);
        AuthRefreshTokenRepository tokens = mock(AuthRefreshTokenRepository.class);
        TokenConfig jwt = mock(TokenConfig.class);
        AuthSessionService service = new AuthSessionService(sessions, tokens, jwt);
        User user = usuario();
        when(jwt.generateToken(any(User.class), any(UUID.class))).thenReturn("access");

        AuthResponse initial = service.login(user, AuthClientType.ANDROID);
        ArgumentCaptor<AuthSession> sessionCaptor = ArgumentCaptor.forClass(AuthSession.class);
        verify(sessions).save(sessionCaptor.capture());
        AuthSession session = sessionCaptor.getValue();
        ArgumentCaptor<AuthRefreshToken> tokenCaptor = ArgumentCaptor.forClass(AuthRefreshToken.class);
        verify(tokens).save(tokenCaptor.capture());
        AuthRefreshToken first = tokenCaptor.getValue();
        when(sessions.findByIdForUpdate(session.getId())).thenReturn(Optional.of(session));
        when(tokens.findById(hash(initial.refreshToken()))).thenReturn(Optional.of(first));

        AuthResponse renewed = service.refresh(initial.refreshToken(), AuthClientType.ANDROID);

        assertNotEquals(initial.refreshToken(), renewed.refreshToken());
        assertNotNull(first.getConsumedAt());
        assertThrows(BadCredentialsException.class,
            () -> service.refresh(initial.refreshToken(), AuthClientType.ANDROID));
        assertNotNull(session.getRevokedAt());
    }

    @Test
    void logoutRevogaSomenteSessaoDoTokenInformado() throws Exception {
        AuthSessionRepository sessions = mock(AuthSessionRepository.class);
        AuthRefreshTokenRepository tokens = mock(AuthRefreshTokenRepository.class);
        TokenConfig jwt = mock(TokenConfig.class);
        AuthSessionService service = new AuthSessionService(sessions, tokens, jwt);
        when(jwt.generateToken(any(User.class), any(UUID.class))).thenReturn("access");
        AuthResponse issued = service.login(usuario(), AuthClientType.WEB);
        ArgumentCaptor<AuthSession> sessionCaptor = ArgumentCaptor.forClass(AuthSession.class);
        verify(sessions).save(sessionCaptor.capture());
        AuthSession session = sessionCaptor.getValue();
        ArgumentCaptor<AuthRefreshToken> tokenCaptor = ArgumentCaptor.forClass(AuthRefreshToken.class);
        verify(tokens).save(tokenCaptor.capture());
        when(sessions.findByIdForUpdate(session.getId())).thenReturn(Optional.of(session));
        when(tokens.findById(hash(issued.refreshToken()))).thenReturn(Optional.of(tokenCaptor.getValue()));

        service.logout(issued.refreshToken(), AuthClientType.ANDROID);
        assertNull(session.getRevokedAt());
        service.logout(issued.refreshToken(), AuthClientType.WEB);
        assertNotNull(session.getRevokedAt());
    }

    private User usuario() {
        Agente agente = new Agente();
        agente.setCpf("12345678909");
        User user = new User();
        user.setId(1L);
        user.setAgente(agente);
        user.setRole(UserRole.ROLE_CAMPO);
        user.setStatus(UserStatus.ATIVO);
        return user;
    }

    private String hash(String raw) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
            .digest(raw.getBytes(StandardCharsets.UTF_8)));
    }
}
