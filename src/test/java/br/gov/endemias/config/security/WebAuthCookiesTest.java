package br.gov.endemias.config.security;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;

import br.gov.endemias.dto.AuthResponse;

class WebAuthCookiesTest {
    @Test
    void cookiesDeProducaoSaoHostOnlySecureHttpOnlyESameSiteLax() {
        WebAuthCookies cookies = new WebAuthCookies(true);
        MockHttpServletResponse response = new MockHttpServletResponse();

        cookies.issue(response, new AuthResponse("access", "Bearer", "refresh", 900));

        var headers = response.getHeaders(HttpHeaders.SET_COOKIE);
        assertTrue(headers.stream().anyMatch(value -> value.startsWith("__Host-ACCESS_TOKEN=")));
        assertTrue(headers.stream().anyMatch(value -> value.startsWith("__Host-REFRESH_TOKEN=")));
        assertTrue(headers.stream().allMatch(value -> value.contains("HttpOnly")
            && value.contains("Secure") && value.contains("SameSite=Lax")
            && value.contains("Path=/") && !value.contains("Domain=")));
    }

    @Test
    void logoutLimpaAmbosCookies() {
        WebAuthCookies cookies = new WebAuthCookies(false);
        MockHttpServletResponse response = new MockHttpServletResponse();

        cookies.clear(response);

        var headers = response.getHeaders(HttpHeaders.SET_COOKIE);
        assertTrue(headers.stream().anyMatch(value -> value.startsWith("ACCESS_TOKEN=")
            && value.contains("Max-Age=0")));
        assertTrue(headers.stream().anyMatch(value -> value.startsWith("REFRESH_TOKEN=")
            && value.contains("Max-Age=0")));
    }
}
