package br.gov.endemias.controller;

import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.gov.endemias.config.security.JWTUserData;
import br.gov.endemias.config.security.WebAuthCookies;
import br.gov.endemias.domain.entity.User;
import br.gov.endemias.domain.enums.AuthClientType;
import br.gov.endemias.dto.AuthResponse;
import br.gov.endemias.dto.RefreshRequest;
import br.gov.endemias.dto.LoginRequest;
import br.gov.endemias.dto.CadastroPublicoRequest;
import br.gov.endemias.dto.UserResponse;
import br.gov.endemias.service.UserService;
import br.gov.endemias.service.AuthSessionService;
import br.gov.endemias.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final AuthSessionService sessions;
    private final WebAuthCookies cookies;
    private final UserRepository users;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        return sessions.login(authenticate(request), AuthClientType.ANDROID);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody @Valid RefreshRequest request, HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        return sessions.refresh(request.refreshToken(), AuthClientType.ANDROID);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody @Valid RefreshRequest request) {
        sessions.logout(request.refreshToken(), AuthClientType.ANDROID);
    }

    @GetMapping("/web/csrf")
    public Map<String, String> csrf(HttpServletRequest request, HttpServletResponse response) {
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        return Map.of("token", token.getToken(), "headerName", token.getHeaderName());
    }

    @PostMapping("/web/login")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void webLogin(@RequestBody @Valid LoginRequest request, HttpServletRequest servletRequest,
                         HttpServletResponse response) {
        User user = authenticate(request);
        String previous = cookies.read(servletRequest, cookies.refreshName());
        if (previous != null) {
            sessions.logout(previous, AuthClientType.WEB);
        }
        cookies.issue(response, sessions.login(user, AuthClientType.WEB));
    }

    @PostMapping("/web/refresh")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void webRefresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookies.read(request, cookies.refreshName());
        if (refreshToken == null) {
            throw new BadCredentialsException("Refresh token ausente");
        }
        cookies.issue(response, sessions.refresh(refreshToken, AuthClientType.WEB));
    }

    @PostMapping("/web/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void webLogout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookies.read(request, cookies.refreshName());
        if (refreshToken != null) {
            sessions.logout(refreshToken, AuthClientType.WEB);
        }
        cookies.clear(response);
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal JWTUserData principal, HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        return users.findByIdWithAgente(principal.userId())
            .map(UserResponse::fromEntity)
            .orElseThrow(() -> new BadCredentialsException("Usuário inválido"));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody @Valid CadastroPublicoRequest request) {
        return userService.cadastrarPublico(request);
    }

    private User authenticate(LoginRequest request) {
        UsernamePasswordAuthenticationToken credentials =
            new UsernamePasswordAuthenticationToken(request.username(), request.password());
        Authentication auth = authenticationManager.authenticate(credentials);
        return (User) auth.getPrincipal();
    }

}
