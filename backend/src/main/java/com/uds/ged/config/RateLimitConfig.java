package com.uds.ged.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration for rate limiting using Bucket4j.
 * Implements token bucket algorithm to prevent abuse of registration endpoint.
 * 
 * @author GED Team
 * @version 1.0
 * @since 2026-02-22
 */
@Configuration
public class RateLimitConfig {

    /**
     * Creates a bucket cache for storing rate limit buckets per IP address.
     * 
     * @return Map of IP addresses to their corresponding rate limit buckets
     */
    @Bean
    public Map<String, Bucket> bucketCache() {
        return new ConcurrentHashMap<>();
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
