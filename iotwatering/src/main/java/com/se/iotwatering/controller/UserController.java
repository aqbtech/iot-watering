package com.se.iotwatering.controller;

import com.se.iotwatering.service.WebSocketClient;
import jakarta.servlet.ServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/user/v1")
public class UserController {
	private WebSocketClient webSocketClient;
	@PostMapping("/login")
	public ResponseEntity<?> login() {
		log.info("Login request");
		return ResponseEntity.ok("Login success");
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(ServletRequest request) {
		return ResponseEntity.ok().build();
	}

	@GetMapping("/dashboard")
	public ResponseEntity<?> dashboard(ServletRequest request) {
		return ResponseEntity.ok().build();
	}
	@Autowired
	private WebSocketClient wsClient;
	@GetMapping("/send")
	public String sendMessage(@RequestParam String message) {
		try {
			wsClient.subscribeToDevice(message);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return "Sent";
	}
}
