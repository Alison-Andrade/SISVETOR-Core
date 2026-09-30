package br.gov.endemias.config.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import br.gov.endemias.domain.entity.Agente;
import br.gov.endemias.domain.entity.User;
import br.gov.endemias.domain.enums.UserRole;
import br.gov.endemias.domain.enums.UserStatus;
import br.gov.endemias.repository.UserRepository;
import br.gov.endemias.service.AuthSessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.FilterChain;

class SecurityFilterTest {

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void tokenDeUsuarioBloqueadoNaoAutentica() throws Exception {
        TokenConfig tokens = mock(TokenConfig.class);
        UserRepository usuarios = mock(UserRepository.class);
        SecurityFilter filtro = new SecurityFilter(tokens, usuarios, mock(AuthSessionService.class), new WebAuthCookies(false));
        User bloqueado = usuario(UserRole.ROLE_CAMPO, UserStatus.BLOQUEADO);
        when(tokens.validateToken("token")).thenReturn(Optional.of(new JWTUserData(1L, "12345678909", "ROLE_CAMPO")));
        when(usuarios.findByIdWithAgente(1L)).thenReturn(Optional.of(bloqueado));

        filtro.doFilter(requisicao(), new MockHttpServletResponse(), mock(FilterChain.class));

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void usaPapelAtualDoBancoEIgnoraPapelAntigoDoToken() throws Exception {
        TokenConfig tokens = mock(TokenConfig.class);
        UserRepository usuarios = mock(UserRepository.class);
        SecurityFilter filtro = new SecurityFilter(tokens, usuarios, mock(AuthSessionService.class), new WebAuthCookies(false));
        User ativo = usuario(UserRole.ROLE_SUPERVISOR, UserStatus.ATIVO);
        when(tokens.validateToken("token")).thenReturn(Optional.of(new JWTUserData(1L, "12345678909", "ROLE_CAMPO")));
        when(usuarios.findByIdWithAgente(1L)).thenReturn(Optional.of(ativo));

        filtro.doFilter(requisicao(), new MockHttpServletResponse(), mock(FilterChain.class));

        assertEquals("ROLE_SUPERVISOR", SecurityContextHolder.getContext()
            .getAuthentication().getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void cookieAutenticaSemHeader() throws Exception {
        TokenConfig tokens = mock(TokenConfig.class);
        UserRepository usuarios = mock(UserRepository.class);
        AuthSessionService sessions = mock(AuthSessionService.class);
        SecurityFilter filtro = new SecurityFilter(tokens, usuarios, sessions, new WebAuthCookies(false));
        UUID sessionId = UUID.randomUUID();
        when(tokens.validateToken("cookie-token"))
            .thenReturn(Optional.of(new JWTUserData(1L, "12345678909", "ROLE_CAMPO", sessionId)));
        when(sessions.isActive(sessionId, 1L)).thenReturn(true);
        when(usuarios.findByIdWithAgente(1L)).thenReturn(Optional.of(usuario(UserRole.ROLE_CAMPO, UserStatus.ATIVO)));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("ACCESS_TOKEN", "cookie-token"));

        filtro.doFilter(request, new MockHttpServletResponse(), mock(FilterChain.class));

        assertEquals("ROLE_CAMPO", SecurityContextHolder.getContext().getAuthentication()
            .getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void bearerInvalidoNaoRecorreAoCookie() throws Exception {
        TokenConfig tokens = mock(TokenConfig.class);
        UserRepository usuarios = mock(UserRepository.class);
        SecurityFilter filtro = new SecurityFilter(tokens, usuarios, mock(AuthSessionService.class), new WebAuthCookies(false));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalido");
        request.setCookies(new Cookie("ACCESS_TOKEN", "cookie-token"));
        when(tokens.validateToken("invalido")).thenReturn(Optional.empty());

        filtro.doFilter(request, new MockHttpServletResponse(), mock(FilterChain.class));

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void sessaoRevogadaNaoAutentica() throws Exception {
        TokenConfig tokens = mock(TokenConfig.class);
        UserRepository usuarios = mock(UserRepository.class);
        AuthSessionService sessions = mock(AuthSessionService.class);
        SecurityFilter filtro = new SecurityFilter(tokens, usuarios, sessions, new WebAuthCookies(false));
        UUID sessionId = UUID.randomUUID();
        when(tokens.validateToken("token"))
            .thenReturn(Optional.of(new JWTUserData(1L, "12345678909", "ROLE_CAMPO", sessionId)));
        when(sessions.isActive(sessionId, 1L)).thenReturn(false);

        filtro.doFilter(requisicao(), new MockHttpServletResponse(), mock(FilterChain.class));

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    private MockHttpServletRequest requisicao() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");
        return request;
    }

    private User usuario(UserRole role, UserStatus status) {
        Agente agente = new Agente();
        agente.setCpf("12345678909");
        User user = new User();
        user.setId(1L);
        user.setAgente(agente);
        user.setRole(role);
        user.setStatus(status);
        return user;
    }
}
