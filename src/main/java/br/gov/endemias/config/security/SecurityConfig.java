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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.Customizer;

import java.util.Arrays;
import java.util.List;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityFilter securityFilter;
    private final WebAuthCookies cookies;

    @Value("${auth.web.allowed-origins:}")
    private String allowedOrigins;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        CookieCsrfTokenRepository csrfRepository = new CookieCsrfTokenRepository();
        csrfRepository.setCookieName(cookies.csrfName());
        csrfRepository.setCookieCustomizer(builder -> builder.path("/").secure(cookies.secure())
            .httpOnly(true).sameSite("Lax"));
        return http
            .csrf(csrf -> csrf.csrfTokenRepository(csrfRepository)
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                .requireCsrfProtectionMatcher(request -> {
                    String method = request.getMethod();
                    if ("GET".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method)
                            || "TRACE".equals(method)) {
                        return false;
                    }
                    String path = request.getServletPath();
                    return path.equals("/api/v1/auth/web/login")
                        || path.equals("/api/v1/auth/web/refresh")
                        || path.equals("/api/v1/auth/web/logout")
                        || cookies.read(request, cookies.accessName()) != null
                        || cookies.read(request, cookies.refreshName()) != null;
                }))
            .cors(Customizer.withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                    .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/actuator/health").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/auth/refresh", "/api/v1/auth/logout").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/auth/web/csrf").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/auth/web/login", "/api/v1/auth/web/refresh",
                        "/api/v1/auth/web/logout").permitAll()
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
                    .requestMatchers(HttpMethod.POST, "/api/v1/ciclos")
                        .hasAnyRole("COORDENADOR", "ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/ciclos/**")
                        .hasAnyRole("COORDENADOR", "ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/ciclos/**")
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
            .map(String::trim).filter(value -> !value.isEmpty()).toList();
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-XSRF-TOKEN"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
