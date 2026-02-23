package com.uds.ged.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration for rate limiting using Bucket4j with Caffeine cache.
 * Implements token bucket algorithm to prevent abuse of registration endpoint.
 * Uses Caffeine cache with automatic expiration to prevent memory leaks.
 * 
 * @author GED Team
 * @version 1.0
 * @since 2026-02-22
 */
@Configuration
public class RateLimitConfig {

    /**
     * Creates a Caffeine cache for storing rate limit buckets per IP address.
     * Cache automatically expires entries after 2 hours of inactivity.
     * Maximum size is limited to 10,000 entries to prevent memory issues.
     * 
     * @return Caffeine cache of IP addresses to their corresponding rate limit buckets
     */
    @Bean
    public Cache<String, Bucket> bucketCache() {
        return Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofHours(2))
                .maximumSize(10_000)
                .recordStats()
                .build();
    }

    /**
     * Creates a rate limit bucket for registration endpoint.
     * Allows 5 registration attempts per hour per IP address.
     * 
     * @return Bucket configured with rate limit rules
     */
    public Bucket createRegistrationBucket() {
        Bandwidth limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofHours(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
