package com.guibsantos.shorterURL.security.filter;

import com.guibsantos.shorterURL.service.RateLimitService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RateLimitFilter implements Filter {

    private final RateLimitService rateLimitService;

    private static final Set<String> BYPASS_PREFIXES = Set.of(
            "/swagger", "/v3/api-docs", "/css", "/swagger-ui"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String method = httpRequest.getMethod();
        String path = httpRequest.getRequestURI();

        if ("OPTIONS".equalsIgnoreCase(method) || isBypassPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        String ip = getClientIP(httpRequest);
        boolean isAuthRoute = path.startsWith("/auth");

        Bucket bucket = rateLimitService.resolveBucket(ip, isAuthRoute);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            httpResponse.addHeader("X-Rate-Limit-Remaining",
                    String.valueOf(probe.getRemainingTokens()));
            chain.doFilter(request, response);
        } else {
            long waitSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000;
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(
                    String.format("{\"error\": \"Muitas tentativas. Aguarde %d segundos.\"}", waitSeconds)
            );
        }
    }

    private boolean isBypassPath(String path) {
        return BYPASS_PREFIXES.stream().anyMatch(path::startsWith);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isBlank()) return request.getRemoteAddr();
        return xfHeader.split(",")[0].trim();
    }
}