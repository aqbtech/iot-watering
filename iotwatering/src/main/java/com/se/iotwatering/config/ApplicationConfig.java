package com.se.iotwatering.config;


import com.se.iotwatering.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.se.iotwatering.entity.User;

@Configuration
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true)
public class ApplicationConfig {
	// This class is used to configure the application for the first run
	@NonFinal
	private static final String ADMIN_USER_NAME = "admin";
	@NonFinal
	private static final String ADMIN_PASSWORD = "admin";
	@Bean
	@ConditionalOnProperty(
		prefix = "spring",
		value = "datasource.driver-class-name",
		havingValue = "com.mysql.cj.jdbc.Driver"
	)
	ApplicationRunner applicationRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		log.info("Initializing application.....");
		return args -> {
			if (!userRepository.existsByUsername(ADMIN_USER_NAME)) {
				User admin = User.builder()
						.username(ADMIN_USER_NAME)
						.password(passwordEncoder.encode(ADMIN_PASSWORD))
						.build();
				userRepository.save(admin);
				log.warn("Admin user created with username: {} and password: {}", ADMIN_USER_NAME, ADMIN_PASSWORD);
			}
			log.info("Application initialized successfully");
		};
	}
}
