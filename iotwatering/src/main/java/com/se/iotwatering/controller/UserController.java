package com.se.iotwatering.controller;

import com.se.iotwatering.dto.http.request.UserProfileUpdateRequest;
import com.se.iotwatering.dto.http.request.UserRegister;
import com.se.iotwatering.dto.http.response.MinimalUserProfile;
import com.se.iotwatering.dto.http.response.ResponseAPITemplate;
import com.se.iotwatering.dto.http.response.UserProfileResponse;
import com.se.iotwatering.service.GuestService;
import com.se.iotwatering.service.UserService;
import com.se.iotwatering.service.impl.WebSocketClient;
import jakarta.servlet.ServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user/v1")
@RequiredArgsConstructor
public class UserController {
	private final GuestService guestService;
	private final UserService userService;
	private WebSocketClient webSocketClient;

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
	
	@PutMapping("/profile")
	public ResponseAPITemplate<?> updateProfile(@RequestBody UserProfileUpdateRequest request) {
		boolean result = userService.updateProfile(request);
		return ResponseAPITemplate.builder()
				.result(result ? "Cập nhật thành công" : "Không thể cập nhật hồ sơ")
				.build();
	}
	
	@GetMapping("/profile/{username}")
	public ResponseAPITemplate<UserProfileResponse> getUserProfile(@PathVariable String username) {
		UserProfileResponse profile = userService.getUserProfile(username);
		return ResponseAPITemplate.<UserProfileResponse>builder()
				.result(profile)
				.build();
	}
}
