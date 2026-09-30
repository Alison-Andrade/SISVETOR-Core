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

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {
    
    private final TokenConfig tokenConfig;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring("Bearer ".length());
            tokenConfig.validateToken(token)
                .filter(data -> data.userId() != null && data.cpf() != null)
                .flatMap(data -> userRepository.findByIdWithAgente(data.userId())
                    .filter(User::isEnabled)
                    .filter(user -> data.cpf().equals(user.getAgente().getCpf())))
                .ifPresent(user -> {
                    JWTUserData currentUser = new JWTUserData(
                        user.getId(), user.getAgente().getCpf(), user.getRole().name());
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(currentUser, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });
        }
        filterChain.doFilter(request, response);
    }
}
