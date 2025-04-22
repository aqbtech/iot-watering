package com.se.iotwatering.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class Security {
	// This class is used to configure security for the application
	// If you want to use security, you can add configurations here
	private final String[] PUBLIC_ENDPOINTS = {
			"/auth/token", "/auth/introspect", "/auth/refresh",
			"user/v1/register"
	};
	private final CustomJwtDecoder customJwtDecoder;

	public Security(CustomJwtDecoder customJwtDecoder) {
		this.customJwtDecoder = customJwtDecoder;
	}
	@Value("${security.cors.allowed-origins}")
	private String allowedOrigin;
	@Value("${security.cors.allowed-headers}")
	private String allowedHeader;
	@Value("${security.cors.allowed-methods}")
	private String allowedMethod;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
		security.authorizeHttpRequests(request -> request
				.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
				.requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINTS).permitAll()
				.anyRequest().authenticated());

		security.oauth2ResourceServer(oauth2 -> oauth2
				.jwt(jwtConfigurer -> jwtConfigurer
						.decoder(customJwtDecoder)
						.jwtAuthenticationConverter(new CustomJwtAuthenticationConverter())
				).authenticationEntryPoint(new JwtAuthEntryPoint()));
		security.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		security.csrf(AbstractHttpConfigurer::disable);
		security.cors(cors -> cors.configurationSource(request -> {
			CorsConfiguration corsConfig = new CorsConfiguration();
			corsConfig.addAllowedOrigin(allowedOrigin);
			corsConfig.addAllowedMethod(allowedMethod);
			corsConfig.addAllowedHeader(allowedHeader);
			return corsConfig;
		}));
		return security.build();
	}


	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10);
	}
}
