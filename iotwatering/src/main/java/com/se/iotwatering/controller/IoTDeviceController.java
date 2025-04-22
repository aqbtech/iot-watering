package com.se.iotwatering.controller;

import com.se.iotwatering.dto.http.request.DeviceAddRequest;
import com.se.iotwatering.dto.http.request.DeviceConfigRequest;
import com.se.iotwatering.dto.http.request.DeviceStateRequest;
import com.se.iotwatering.dto.http.response.DeviceInfoResponse;
import com.se.iotwatering.dto.http.response.ResponseAPITemplate;
import com.se.iotwatering.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/device")
@RequiredArgsConstructor
public class IoTDeviceController {

	private final DeviceService deviceService;

	@PostMapping("/add")
	public ResponseAPITemplate<?> addDevice(@Valid @RequestBody DeviceAddRequest request) {
		boolean result = deviceService.addDevice(request);
		return ResponseAPITemplate.builder()
				.result(result ? "Thiết bị đã được thêm" : "Không thể thêm thiết bị")
				.build();
	}

	@PostMapping("/setConfig")
	public ResponseAPITemplate<?> setConfig(@RequestBody DeviceConfigRequest request) {
		boolean result = deviceService.setConfig(request);
		return ResponseAPITemplate.builder()
				.result(result ? "Đã cập nhật cấu hình" : "Không thể cập nhật cấu hình")
				.build();
	}

	@PostMapping("/light")
	public ResponseAPITemplate<?> controlLight(@RequestBody DeviceStateRequest request) {
		boolean result = deviceService.controlLight(request);
		return ResponseAPITemplate.builder()
				.result(result ? "Đã cập nhật trạng thái đèn" : "Không thể cập nhật trạng thái đèn")
				.build();
	}

	@PostMapping("/pump")
	public ResponseAPITemplate<?> controlPump(@RequestBody DeviceStateRequest request) {
		boolean result = deviceService.controlPump(request);
		return ResponseAPITemplate.builder()
				.result(result ? "Đã cập nhật trạng thái máy bơm" : "Không thể cập nhật trạng thái máy bơm")
				.build();
	}

	@PostMapping("/siren")
	public ResponseAPITemplate<?> controlSiren(@RequestBody DeviceStateRequest request) {
		boolean result = deviceService.controlSiren(request);
		return ResponseAPITemplate.builder()
				.result(result ? "Đã cập nhật trạng thái còi" : "Không thể cập nhật trạng thái còi")
				.build();
	}

	@PostMapping("/fan")
	public ResponseAPITemplate<?> controlFan(@RequestBody DeviceStateRequest request) {
		boolean result = deviceService.controlFan(request);
		return ResponseAPITemplate.builder()
				.result(result ? "Đã cập nhật trạng thái quạt" : "Không thể cập nhật trạng thái quạt")
				.build();
	}

	@GetMapping("/info")
	public ResponseAPITemplate<DeviceInfoResponse> getDeviceInfo(@RequestParam String deviceId) {
		DeviceInfoResponse deviceInfo = deviceService.getDeviceInfo(deviceId);
		return ResponseAPITemplate.<DeviceInfoResponse>builder()
				.result(deviceInfo)
				.build();
	}
	@GetMapping("/info/v2")
	public ResponseAPITemplate<DeviceInfoResponse> getDeviceInfo(@RequestParam Long deviceId) {
		DeviceInfoResponse deviceInfo = deviceService.getDeviceDetail(deviceId);
		return ResponseAPITemplate.<DeviceInfoResponse>builder()
				.result(deviceInfo)
				.build();
	}
}
