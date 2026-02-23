package com.uds.ged.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Web configuration for the application.
 * Configures Jackson ObjectMapper and other web-related beans.
 * 
 * @author GED Team
 * @version 1.0
 * @since 2026-02-22
 */
@Configuration
public class WebConfig {

    /**
     * Configures and provides an ObjectMapper bean for JSON serialization/deserialization.
     * Includes Java 8 date/time support and pretty printing for development.
     * 
     * @return configured ObjectMapper instance
     */
    @Bean
    public ObjectMapper objectMapper() {
        return Jackson2ObjectMapperBuilder.json()
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }
}
