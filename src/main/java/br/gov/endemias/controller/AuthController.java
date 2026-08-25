package br.gov.endemias.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.endemias.config.security.TokenConfig;
import br.gov.endemias.domain.entity.User;
import br.gov.endemias.dto.AuthResponse;
import br.gov.endemias.dto.LoginRequest;
import br.gov.endemias.dto.UserRequest;
import br.gov.endemias.dto.UserResponse;
import br.gov.endemias.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest request) {
        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(request.username(), request.password());
        Authentication auth = authenticationManager.authenticate(userAndPass);

        User user = (User) auth.getPrincipal();
        String token = tokenConfig.generateToken(user);

        return new AuthResponse(token);
    }

    @PostMapping("/register")
    public UserResponse register(@RequestBody @Valid UserRequest request) {
        return userService.cadastrar(request);
    }
    

}
