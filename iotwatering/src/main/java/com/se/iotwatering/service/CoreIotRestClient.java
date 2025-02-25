package com.se.iotwatering.service;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class CoreIotRestClient {
	private final WebClient webClient;

	public CoreIotRestClient(WebClient webClient) {
		this.webClient = webClient;
	}

	public String login(String username, String password, String url) {
		return webClient.post()
				.uri(url)
				.header("Content-Type", "application/json")
				.bodyValue(Map.of("username", username, "password", password))
				.retrieve()
				.bodyToMono(String.class)
				.block();
	}
}
