package com.se.iotwatering.service.impl;

import com.se.iotwatering.config.CoreIotConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoreIotRestClient {
	private final WebClient webClient;
	private final ApplicationContext applicationContext;

	//	public String login(String username, String password, String url) {
//		return webClient.post()
//				.uri(url)
//				.header("Content-Type", "application/json")
//				.bodyValue(Map.of("username", username, "password", password))
//				.retrieve()
//				.bodyToMono(String.class)
//				.block();
//	}
	public String login(String username, String password, String url) {
		return sendRequest("POST", url, null, Map.of("username", username, "password", password));
	}

	/**
	 * Gửi HTTP request với method chỉ định, có thể có query parameters và body.
	 *
	 * @param method      Phương thức HTTP (GET, POST, PUT, DELETE, ...)
	 * @param uri         URL gốc của request
	 * @param queryParams Map chứa các query parameter (có thể null nếu không có)
	 * @param body        Body của request (thường là JSON) (có thể null nếu không cần)
	 * @return Kết quả trả về dưới dạng String
	 */
	public String sendRequest(String method, String uri, Map<String, Object> queryParams, Object body) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(uri);
		if (queryParams != null && !queryParams.isEmpty()) {
			queryParams.forEach(uriBuilder::queryParam);
		}
		// Sử dụng build() để builder tự encode lại các giá trị
		String finalUri = uriBuilder.build().toUriString();
		log.info("Final URI: {}", finalUri);
		CoreIotConfig coreIotConfig = applicationContext.getBean(CoreIotConfig.class);
		String token = coreIotConfig.getJwtToken();
		WebClient.RequestBodySpec requestSpec = webClient.method(HttpMethod.valueOf(method))
				.uri(finalUri)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.header("X-Authorization", "Bearer " + token);

		if (body != null && !"GET".equalsIgnoreCase(method)) {
			return requestSpec.bodyValue(body)
					.retrieve()
					.bodyToMono(String.class)
					.block();
		} else {
			return requestSpec.retrieve()
					.bodyToMono(String.class)
					.block();
		}
	}
}