package com.se.iotwatering.controller;

import com.se.iotwatering.dto.DeviceInfo;
import com.se.iotwatering.dto.SensorData;
import com.se.iotwatering.dto.http.response.ResponseAPITemplate;
import com.se.iotwatering.entity.Sensor;
import com.se.iotwatering.service.DeviceControllerService;
import com.se.iotwatering.service.TelemetryService;
import com.se.iotwatering.service.WebSocketClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/device/v1")
@RequiredArgsConstructor
public class DeviceController {
	@Autowired
	private WebSocketClient wsClient;
	private final DeviceControllerService deviceControllerService;

	@GetMapping("/subscribe")
	public ResponseAPITemplate<?> subDevice(@RequestParam("dvcId") String deviceId) {
		try {
			wsClient.subscribeToDevice(deviceId);
			String topic = "device/" + deviceId;
			return ResponseAPITemplate.<String>builder()
					.result(topic)
					.build();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@GetMapping("/list-device")
	public ResponseAPITemplate<Page<DeviceInfo>> listDevice(@AuthenticationPrincipal Jwt jwt,
												   @RequestParam("page") int page,
												   @RequestParam("size") int size) {
		//TODO: Get list device
		Page<DeviceInfo> dvcs = deviceControllerService.listDevice(page, size);
		return ResponseAPITemplate.<Page<DeviceInfo>>builder()
				.result(dvcs)
				.build();
	}

	@PostMapping("/trigger")
	public ResponseAPITemplate<?> triggerDevice(@RequestParam("dvcId") long deviceId,
												@RequestParam(value = "duration", required = false) String duration) {
		return ResponseAPITemplate.<String>builder()
				.result(deviceControllerService.triggerPump(deviceId))
				.build();
	}@GetMapping("/detail")
	public ResponseAPITemplate<?> triggerDevice(@RequestParam("dvcId") long deviceId) {
		return ResponseAPITemplate.<Sensor>builder()
				.result(deviceControllerService.getDeviceById(deviceId))
				.build();
	}

	@GetMapping("/history")
	public ResponseAPITemplate<Page<SensorData>> getHistory(@RequestParam("dvcId") long deviceId,
												   @RequestParam(value = "from", required = false) String from,
												   @RequestParam(value = "to", required = false) String to,
												   @RequestParam("page") int page,
												   @RequestParam("size") int size) {
		Page<SensorData> sensorData = telemetryService.getHistory(deviceId, from, to, page, size);
		return ResponseAPITemplate.<Page<SensorData>>builder()
				.result(sensorData)
				.build();
	}

	@PostMapping("/threshold-config")
	public ResponseAPITemplate<?> configThreshold(@RequestParam("dvcId") String deviceId,
												  @RequestParam("threshold") double thresholdReq) {
		//TODO: Config threshold
		return new ResponseAPITemplate<>();
	}

	private final TelemetryService telemetryService;
	@GetMapping("/latest-telemetry")
	public ResponseAPITemplate<?> getLatestTelemetry(@RequestParam("dvcId") long deviceId) {
		SensorData sensorData = telemetryService.getLatestTelemetry(deviceId);
		return ResponseAPITemplate.<SensorData>builder()
				.result(sensorData)
				.build();
	}
}
