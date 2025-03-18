package com.se.iotwatering.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.iotwatering.entity.SensorData;
import com.se.iotwatering.exception.ErrorCode;
import com.se.iotwatering.exception.WebServerException;
import com.se.iotwatering.mapper.SensorPayload;
import com.se.iotwatering.repo.SensorDataRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@Slf4j
@RequiredArgsConstructor
public class HandlerDataWare implements DataObserver {
	private final SensorDataRepo sensorRepository;

	@Override
	public void onMessage(String payload) {
//		SensorData sensorData = save(payload);
		SensorData sensorData = dataMapper(payload);
		thresholdCheck(sensorData);
	}

	private void thresholdCheck(SensorData sensorData) {
		// Check if sensorData is out of threshold
		log.info("Checking threshold for sensor data: {}", sensorData);
	}

	private SensorData save(String payload) {
		SensorData sensorData = dataMapper(payload);
		log.info("Saving sensor data: {}", sensorData);
		return sensorRepository.save(sensorData);
	}

	private SensorData dataMapper(String payload) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			SensorPayload sensorPayload = objectMapper.readValue(payload, SensorPayload.class);

			SensorData sensorData = new SensorData();
			sensorData.setHumidity(sensorPayload.data.humidity.getFirst().get(1).toString());
			sensorData.setLight(sensorPayload.data.light.getFirst().get(1).toString());
			sensorData.setTemperature(Integer.parseInt(sensorPayload.data.temperature.getFirst().get(1).toString()));
			sensorData.setSoilMoisture(sensorPayload.data.soil.getFirst().get(1).toString());
			// Lấy timestamp mới nhất từ latestValues
			long timestamp = sensorPayload.latestValues.get("temperature"); // Chọn 1 timestamp làm chuẩn
			sensorData.setMeasuredTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()));

			return sensorData;
		} catch (Exception e) {
			log.error("Error when mapping payload to SensorData: {}", e.getMessage());
			throw new WebServerException(ErrorCode.UNKNOWN_ERROR);
		}
	}
}
