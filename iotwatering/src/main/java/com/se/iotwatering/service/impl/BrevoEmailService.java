package com.se.iotwatering.service.impl;

import com.se.iotwatering.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrevoEmailService implements com.se.iotwatering.service.EmailService {
    @Value("${brevo.api.key}")
    private String apiKey;
    @Value("${brevo.sender.email}")
    private String senderEmail;
    @Value("${brevo.sender.name}")
    private String senderName;

    private final WebClient webClient;

    public BrevoEmailService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.brevo.com/v3").build();
    }

    @Override
    public void sendEmail(String to, String subject, String htmlContent, String toName) {
        Map<String, Object> payload = Map.of(
                "sender", Map.of("name", senderName, "email", senderEmail),
                "to", List.of(Map.of("email", to, "name", toName)),
                "subject", subject,
                "htmlContent", htmlContent
        );
        webClient.post()
                .uri("/smtp/email")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(res -> log.info("Email sent successfully to {}", to))
                .doOnError(err -> log.error("Email failed to {}: {}", to, err.getMessage()))
                .subscribe();
    }
}
