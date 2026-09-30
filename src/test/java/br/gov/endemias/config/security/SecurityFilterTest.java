package br.gov.endemias.config.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

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
        SecurityFilter filtro = new SecurityFilter(tokens, usuarios);
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
        SecurityFilter filtro = new SecurityFilter(tokens, usuarios);
        User ativo = usuario(UserRole.ROLE_SUPERVISOR, UserStatus.ATIVO);
        when(tokens.validateToken("token")).thenReturn(Optional.of(new JWTUserData(1L, "12345678909", "ROLE_CAMPO")));
        when(usuarios.findByIdWithAgente(1L)).thenReturn(Optional.of(ativo));

        filtro.doFilter(requisicao(), new MockHttpServletResponse(), mock(FilterChain.class));

        assertEquals("ROLE_SUPERVISOR", SecurityContextHolder.getContext()
            .getAuthentication().getAuthorities().iterator().next().getAuthority());
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
