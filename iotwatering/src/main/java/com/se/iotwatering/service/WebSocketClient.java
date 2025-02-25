package com.se.iotwatering.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.iotwatering.config.CoreIotConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class WebSocketClient extends TextWebSocketHandler {
	private final ObjectMapper objectMapper = new ObjectMapper();
	private WebSocketSession session;
	private CoreIotConfig coreIotConfig;

	@Autowired
	protected void setCoreIotConfig(CoreIotConfig coreIotConfig) {
		this.coreIotConfig = coreIotConfig;
	}

	@PostConstruct
	public void connect() {
		try {
			String url = "ws://" + coreIotConfig.getConfig().get("url") + "/api/ws";
			StandardWebSocketClient client = new StandardWebSocketClient();
			client.doHandshake(this, url);
			log.info("Attempting to connect to WebSocket: {}", url);
		} catch (Exception e) {
			log.error("Error when connecting to WebSocket: {}", e.getMessage());
			retryConnect();
		}
	}

	@Override
	public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
		this.session = session;
		log.info("WebSocket connected successfully!");
		sendAuthMessage();
	}

	private void sendAuthMessage() throws IOException {
		String jwtToken = coreIotConfig.getJwtToken();
		Map<String, Object> authCmd = Map.of(
				"authCmd", Map.of("cmdId", 0, "token", jwtToken)
		);
		sendMessage(authCmd);
	}

	public void subscribeToDevice(String entityId) throws IOException {
		Map<String, Object> subscriptionCmd = Map.of(
				"cmds", new Object[]{
						Map.of(
								"cmdId", 10,
								"entityType", "DEVICE",
								"entityId", entityId,
								"scope", "LATEST_TELEMETRY",
								"keys", "temperature,humidity",
								"type", "TIMESERIES"
						)
				}
		);
		sendMessage(subscriptionCmd);
	}

	private void sendMessage(Map<String, Object> message) throws IOException {
		if (session != null && session.isOpen()) {
			String jsonMessage = objectMapper.writeValueAsString(message);
			session.sendMessage(new TextMessage(jsonMessage));
		} else {
			log.warn("WebSocket session is closed. Cannot send message.");
		}
	}

	@Override
	protected void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) throws Exception {
		log.info("Received data: {}", message.getPayload());
		// Nếu nhận được lỗi "JWT_EXPIRED", cần reconnect
		if (message.getPayload().contains("JWT_EXPIRED")) {
			log.warn("JWT token expired. Reconnecting...");
			reconnect();
		}
	}

	@Override
	public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {
		log.warn("WebSocket closed with status: {}. Reconnecting...", status);
		retryConnect();
	}

	private void retryConnect() {
		new Thread(() -> {
			try {
				TimeUnit.SECONDS.sleep(5);
				log.info("Retrying WebSocket connection...");
				connect();
			} catch (InterruptedException e) {
				log.error("Retry connection interrupted: {}", e.getMessage());
			}
		}).start();
	}

	private void reconnect() {
		try {
			if (session != null) {
				session.close();
			}
		} catch (IOException e) {
			log.error("Error closing WebSocket session: {}", e.getMessage());
		}
		retryConnect();
	}
}
