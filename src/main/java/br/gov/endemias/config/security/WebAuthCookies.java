package br.gov.endemias.config.security;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import br.gov.endemias.dto.AuthResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class WebAuthCookies {
    private final boolean secure;

    public WebAuthCookies(@Value("${auth.cookie.secure:true}") boolean secure) {
        this.secure = secure;
    }

    public String accessName() {
        return secure ? "__Host-ACCESS_TOKEN" : "ACCESS_TOKEN";
    }

    public String refreshName() {
        return secure ? "__Host-REFRESH_TOKEN" : "REFRESH_TOKEN";
    }

    public String csrfName() {
        return secure ? "__Host-XSRF-TOKEN" : "XSRF-TOKEN";
    }

    public boolean secure() {
        return secure;
    }

    public String read(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public void issue(HttpServletResponse response, AuthResponse tokens) {
        add(response, accessName(), tokens.token(), Duration.ofSeconds(tokens.expiresIn()));
        add(response, refreshName(), tokens.refreshToken(), Duration.ofDays(30));
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
    }

    public void clear(HttpServletResponse response) {
        add(response, accessName(), "", Duration.ZERO);
        add(response, refreshName(), "", Duration.ZERO);
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
    }

    private void add(HttpServletResponse response, String name, String value, Duration age) {
        response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from(name, value)
            .httpOnly(true).secure(secure).sameSite("Lax").path("/").maxAge(age).build().toString());
    }
}
