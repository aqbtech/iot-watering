package com.se.iotwatering.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.iotwatering.entity.SensorData;
import com.se.iotwatering.entity.Sensor;
import com.fasterxml.jackson.databind.JsonNode;
import com.se.iotwatering.exception.BaseErrorCode;
import com.se.iotwatering.exception.DeviceErrorCode;
import com.se.iotwatering.exception.WebServerException;
import com.se.iotwatering.mapper.SensorPayload;
import com.se.iotwatering.repo.SensorDataRepo;
import com.se.iotwatering.repo.SensorRepository;
import com.se.iotwatering.service.DataObserver;
import com.se.iotwatering.service.SensorDataService;
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
	private final SensorDataRepo sensorDataRepository;
	private final SensorRepository sensorRepository;
	private final SensorDataService sensorDataService;
	@Override
	public void onMessage(String entityId, String payload) {
		SensorData sensorData = dataMapper(payload);
		// entityId có thể được sử dụng ở đây nếu cần
		Sensor sensor = sensorRepository.findByPureSensorId(entityId)
				.orElseThrow(() -> new WebServerException(DeviceErrorCode.DEVICE_NOT_FOUND));
		sensorData.setSensor(sensor);
		thresholdCheck(sensorData);
	}

	
	public void thresholdCheck(SensorData sensorData) {
		// Delegate threshold logic to SensorDataService
		sensorDataService.handleSensorData(sensorData);
	}

	private SensorData save(String payload) {
		SensorData sensorData = dataMapper(payload);
		log.info("Saving sensor data: {}", sensorData);
		return sensorDataRepository.save(sensorData);
	}

	private SensorData dataMapper(String payload) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            SensorPayload sensorPayload = objectMapper.readValue(payload, SensorPayload.class);

            SensorData sensorData = new SensorData();
            sensorData.setHumidity(sensorPayload.data.humidity.getFirst().get(1).toString());
            sensorData.setLight(sensorPayload.data.light.getFirst().get(1).toString());
            sensorData.setTemperature(sensorPayload.data.temperature.getFirst().get(1).toString());
            sensorData.setSoilMoisture(sensorPayload.data.soil.getFirst().get(1).toString());
            // Lấy timestamp mới nhất từ latestValues
            long timestamp = sensorPayload.latestValues.get("temperature"); // Chọn 1 timestamp làm chuẩn
            sensorData.setMeasuredTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()));
            return sensorData;
        } catch (Exception e) {
            log.error("Error when mapping payload to SensorData: {}", e.getMessage());
            throw new WebServerException(BaseErrorCode.UNKNOWN_ERROR);
        }
    }
}
