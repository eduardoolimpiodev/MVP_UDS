package com.uds.ged.config;

import com.uds.ged.domain.model.User;
import com.uds.ged.domain.model.enums.UserRole;
import com.uds.ged.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initializeDefaultUsers() {
        return args -> {
            if (userRepository.count() == 0) {
                log.info("Initializing default users...");

                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("password123"));
                admin.setEmail("admin@ged.com");
                admin.setRole(UserRole.ADMIN);
                userRepository.save(admin);

                User user = new User();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("password123"));
                user.setEmail("user@ged.com");
                user.setRole(UserRole.USER);
                userRepository.save(user);

                log.info("Default users created successfully: admin and user");
            } else {
                log.debug("Users already exist, skipping initialization");
            }
        };
    }
}
