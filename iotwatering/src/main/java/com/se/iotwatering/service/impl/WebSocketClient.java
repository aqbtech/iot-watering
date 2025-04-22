package com.se.iotwatering.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.se.iotwatering.config.CoreIotConfig;
import com.se.iotwatering.service.DataObserver;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class WebSocketClient extends TextWebSocketHandler {
	private final ObjectMapper objectMapper = new ObjectMapper();
	private WebSocketSession session;
	private CoreIotConfig coreIotConfig;
	private List<DataObserver> observers = new ArrayList<>();
	private ApplicationContext applicationContext;

	// Static mapping for cmdId <-> entityId
	private static final java.util.concurrent.ConcurrentHashMap<Integer, String> CMDID_TO_ENTITY_MAP = new java.util.concurrent.ConcurrentHashMap<>();
	private static final java.util.concurrent.atomic.AtomicInteger CMDID_SEQ = new java.util.concurrent.atomic.AtomicInteger(10);

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
		int cmdId = CMDID_SEQ.getAndIncrement();
		CMDID_TO_ENTITY_MAP.put(cmdId, entityId);
		Map<String, Object> subscriptionCmd = Map.of(
			"cmds", new Object[]{
				Map.of(
					"cmdId", cmdId,
					"entityType", "DEVICE",
					"entityId", entityId,
					"scope", "LATEST_TELEMETRY",
					"keys", "temperature,humidity,light,soil",
					"type", "TIMESERIES"
				)
			}
		);
		sendMessage(subscriptionCmd);
		if (observers.stream().noneMatch(o -> o instanceof HandlerDataWare)) {
			addObserver(applicationContext.getBean(HandlerDataWare.class));
		}
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
        if (message.getPayload().contains("JWT_EXPIRED")) {
            log.warn("JWT token expired. Reconnecting...");
            reconnect();
        }
        String payload = message.getPayload();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(payload);

        // Lấy subscriptionId/cmdId từ payload (ThingsBoard trả về subscriptionId cho mỗi update)
        int cmdId = -1;
        if (rootNode.has("subscriptionId")) {
            cmdId = rootNode.get("subscriptionId").asInt();
        } else if (rootNode.has("cmdId")) {
            cmdId = rootNode.get("cmdId").asInt();
        }
        String entityId = CMDID_TO_ENTITY_MAP.getOrDefault(cmdId, "unknown");

        // Truyền nguyên payload cho observer để dataMapper xử lý đúng format của ThingsBoard
        notifyObservers(entityId, payload);
    }

    public void addObserver(DataObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(String entityId, String payload) {
        observers.forEach(observer -> observer.onMessage(entityId, payload));
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

	@Autowired
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
