package com.uds.ged.infrastructure.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.uds.ged.application.dto.response.ApiResponse;
import com.uds.ged.config.RateLimitConfig;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter to implement rate limiting on registration endpoint.
 * Uses token bucket algorithm to limit registration attempts per IP address.
 * 
 * @author GED Team
 * @version 1.0
 * @since 2026-02-22
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final Cache<String, Bucket> bucketCache;
    private final RateLimitConfig rateLimitConfig;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        
        if (requestURI.contains("/api/auth/register") && "POST".equals(request.getMethod())) {
            String clientIp = getClientIP(request);
            Bucket bucket = bucketCache.get(clientIp, 
                    k -> rateLimitConfig.createRegistrationBucket());
            
            if (bucket.tryConsume(1)) {
                log.debug("Rate limit check passed for IP: {}", clientIp);
                filterChain.doFilter(request, response);
            } else {
                log.warn("Rate limit exceeded for IP: {} on registration endpoint", clientIp);
                sendRateLimitExceededResponse(response);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Extracts client IP address from request, considering proxy headers.
     * 
     * @param request the HTTP request
     * @return client IP address
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    /**
     * Sends HTTP 429 (Too Many Requests) response when rate limit is exceeded.
     * 
     * @param response the HTTP response
     * @throws IOException if writing response fails
     */
    private void sendRateLimitExceededResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        ApiResponse<Void> apiResponse = ApiResponse.error(
                "Too many registration attempts. Please try again later.");
        
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
