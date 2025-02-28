package com.se.iotwatering.controller;

import com.se.iotwatering.dto.http.request.UserRegister;
import com.se.iotwatering.dto.http.response.MinimalUserProfile;
import com.se.iotwatering.dto.http.response.ResponseAPITemplate;
import com.se.iotwatering.service.GuestService;
import com.se.iotwatering.service.WebSocketClient;
import jakarta.servlet.ServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user/v1")
@RequiredArgsConstructor
public class UserController {
	private WebSocketClient webSocketClient;
	private final GuestService guestService;

	@PostMapping("/register")
	public ResponseAPITemplate<?> register(@Valid @RequestBody UserRegister userRegister, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return ResponseAPITemplate.<List<ObjectError>>builder()
					.code(400)
					.message("Invalid input")
					.result(bindingResult.getAllErrors())
					.build();
		}
		MinimalUserProfile res = guestService.register(userRegister);
		return ResponseAPITemplate.<MinimalUserProfile>builder()
				.result(res)
				.build();
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
