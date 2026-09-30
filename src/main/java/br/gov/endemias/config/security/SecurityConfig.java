package br.gov.endemias.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityFilter securityFilter;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configure(http))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                    .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                    .requestMatchers("/api/v1/usuarios", "/api/v1/usuarios/**")
                        .hasAnyRole("COORDENADOR", "ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/v1/agentes", "/api/v1/agentes/**")
                        .hasAnyRole("SUPERVISOR", "COORDENADOR", "ADMIN")
                    .requestMatchers("/api/v1/agentes", "/api/v1/agentes/**")
                        .hasAnyRole("COORDENADOR", "ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/v1/tratamentos", "/api/v1/tratamentos/**")
                        .hasAnyRole("SUPERVISOR", "COORDENADOR", "ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/v1/tratamentos")
                        .hasAnyRole("CAMPO", "SUPERVISOR", "COORDENADOR", "ADMIN")
                    .requestMatchers(HttpMethod.POST, "/ciclos")
                        .hasAnyRole("COORDENADOR", "ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/ciclos/**")
                        .hasAnyRole("COORDENADOR", "ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/ciclos/**")
                        .hasAnyRole("COORDENADOR", "ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/v1/imoveis")
                        .hasAnyRole("CAMPO", "SUPERVISOR", "COORDENADOR", "ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/v1/imoveis/**")
                        .hasAnyRole("SUPERVISOR", "COORDENADOR", "ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/imoveis/**")
                        .hasAnyRole("SUPERVISOR", "COORDENADOR", "ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/imoveis/**")
                        .hasAnyRole("SUPERVISOR", "COORDENADOR", "ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/v1/localidades", "/api/v1/area",
                        "/api/v1/quarteiroes", "/api/v1/lados")
                        .hasAnyRole("SUPERVISOR", "COORDENADOR", "ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/localidades/**", "/api/v1/area/**",
                        "/api/v1/quarteiroes/**", "/api/v1/lados/**")
                        .hasAnyRole("SUPERVISOR", "COORDENADOR", "ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/localidades/**", "/api/v1/area/**",
                        "/api/v1/quarteiroes/**", "/api/v1/lados/**")
                        .hasAnyRole("SUPERVISOR", "COORDENADOR", "ADMIN")
                    .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, exception) ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .accessDeniedHandler((request, response, exception) ->
                    response.sendError(HttpServletResponse.SC_FORBIDDEN)))
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
