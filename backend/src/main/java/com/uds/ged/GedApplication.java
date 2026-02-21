package com.uds.ged;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GedApplication {

    public static void main(String[] args) {
        SpringApplication.run(GedApplication.class, args);
    }
}
