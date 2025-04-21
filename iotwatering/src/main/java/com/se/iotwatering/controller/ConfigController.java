package com.se.iotwatering.controller;

import com.se.iotwatering.dto.http.request.AlertConfig;
import com.se.iotwatering.dto.http.response.ResponseAPITemplate;
import com.se.iotwatering.entity.Configuration;
import com.se.iotwatering.entity.Sensor;
import com.se.iotwatering.exception.DeviceErrorCode;
import com.se.iotwatering.exception.WebServerException;
import com.se.iotwatering.repo.ConfigurationRepository;
import com.se.iotwatering.repo.SensorRepository;
import com.se.iotwatering.repository.AlertStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ConfigController {
	// GET config/granted?deviceId(long) - get cái trạng thái của cái nút
	//POST config/granted, body {deviceId: long, status: boolean}
	private final ConfigurationRepository configurationRepository;
	private final SensorRepository sensorRepository;

	@GetMapping("config/granted")
	 public ResponseAPITemplate<?> getAlertState(@RequestParam("deviceId") long deviceId) {
		 Sensor sensor = sensorRepository.findById(deviceId)
				 .orElseThrow(() -> new WebServerException(DeviceErrorCode.DEVICE_NOT_FOUND));
		Configuration configuration = configurationRepository.findBySensor(sensor)
				 .orElseThrow(() -> new WebServerException(DeviceErrorCode.CONFIG_NOT_FOUND));
		 return ResponseAPITemplate.<Boolean>builder()
				 .result(configuration.isAutoControlEnabled())
				 .build();
	 }

	 @PostMapping("config/granted")
	 public ResponseAPITemplate<?> setAlertState(@RequestBody AlertConfig alertConfig) {
		 Sensor sensor = sensorRepository.findById(alertConfig.getDeviceId())
				 .orElseThrow(() -> new WebServerException(DeviceErrorCode.DEVICE_NOT_FOUND));
		 Configuration configuration = configurationRepository.findBySensor(sensor)
				 .orElseThrow(() -> new WebServerException(DeviceErrorCode.CONFIG_NOT_FOUND));
		 configuration.setAutoControlEnabled(alertConfig.isStatus());
		 configurationRepository.save(configuration);
		 return ResponseAPITemplate.<Boolean>builder()
				 .result(configuration.isAutoControlEnabled())
				 .build();
	 }
}
