package br.gov.endemias.config.security;

import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import br.gov.endemias.domain.entity.User;
import br.gov.endemias.repository.UserRepository;
import br.gov.endemias.service.AuthSessionService;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {
    
    private final TokenConfig tokenConfig;
    private final UserRepository userRepository;
    private final AuthSessionService authSessions;
    private final WebAuthCookies cookies;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authorizationHeader = request.getHeader("Authorization");

        String token = authorizationHeader != null
            ? (authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : null)
            : cookies.read(request, cookies.accessName());

        if (token != null) {
            tokenConfig.validateToken(token)
                .filter(data -> data.userId() != null && data.cpf() != null)
                .filter(data -> data.sessionId() == null || authSessions.isActive(data.sessionId(), data.userId()))
                .ifPresent(data -> userRepository.findByIdWithAgente(data.userId())
                    .filter(User::isEnabled)
                    .filter(user -> data.cpf().equals(user.getAgente().getCpf()))
                    .ifPresent(user -> {
                        JWTUserData currentUser = new JWTUserData(
                            user.getId(), user.getAgente().getCpf(), user.getRole().name(), data.sessionId());
                        UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(currentUser, null, user.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }));
        }
        filterChain.doFilter(request, response);
    }
}
