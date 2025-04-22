package com.se.iotwatering.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoreIotConfig {
//	private final CoreIotRestClient coreIotRestClient;
	@Value("${coreiot.username}")
	private String username;
	@Value("${coreiot.password}")
	private String password;
	@Value("${coreiot.url}")
	private String url;
	private String token;
	@Getter
	private String refreshToken;
	private final WebClient webClient;

	public String login(String username, String password, String url) {
	return webClient.post()
			.uri(url)
			.header("Content-Type", "application/json")
			.bodyValue(Map.of("username", username, "password", password))
			.retrieve()
			.bodyToMono(String.class)
			.block();
	}
	@PostConstruct
	private void processLogin() {
		// process login at coreiot and save token to this class
		Assert.notNull(username, "Username must not be null");
		Assert.notNull(password, "Password must not be null");
		Assert.notNull(url, "Url must not be null");
		// process login
		// url = "https://app.coreiot.io/api/auth/login";
		String endpoint = "https://" + url + "/api/auth/login";
		var jsonString = login(username, password, endpoint);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			var map = objectMapper.readValue(jsonString, Map.class);
			token = (String) map.get("token");
			refreshToken = (String) map.get("refreshToken");
		} catch (Exception e) {
			log.error("Error when processing login: {}", e.getMessage());
			throw new RuntimeException(e);
		}
//		token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0dXZhbm5ndXllbmFuaHF1YW4xQGdtYWlsLmNvbSIsInVzZXJJZCI6ImNmZGFhZmQwLWYyOTEtMTFlZi04N2I1LTIxYmNjZjdkMjlkNSIsInNjb3BlcyI6WyJURU5BTlRfQURNSU4iXSwic2Vzc2lvbklkIjoiNzZlOGQxMWYtZjMzMi00ODU0LWE3NTItNWI2MmY0OGVkMGM2IiwiZXhwIjoxNzQwNDc2ODQ0LCJpc3MiOiJjb3JlaW90LmlvIiwiaWF0IjoxNzQwNDY3ODQ0LCJmaXJzdE5hbWUiOiJBbmggUXXDom4iLCJlbmFibGVkIjp0cnVlLCJpc1B1YmxpYyI6ZmFsc2UsInRlbmFudElkIjoiY2ZkMjI0NTAtZjI5MS0xMWVmLTg3YjUtMjFiY2NmN2QyOWQ1IiwiY3VzdG9tZXJJZCI6IjEzODE0MDAwLTFkZDItMTFiMi04MDgwLTgwODA4MDgwODA4MCJ9.l-LGGi3g5yFCd_A-6jkdbMAzqOdXEWNK-cFrrRkc0QMTed2aXmH6zoGCsO7tfaPt5LuWGNJn-DuDc9Ozpxh80Q";
//		refreshToken = "";
		Assert.notNull(token, "Token is null, Login to coreiot failed!");
	}

	public String getJwtToken() {
		return token;
	}

	public Map<String, String> getConfig() {
		var rt = new HashMap<String, String>();
		rt.put("username", username);
		rt.put("password", password);
		rt.put("url", url);
		return rt;
	}
}
