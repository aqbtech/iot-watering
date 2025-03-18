package com.se.iotwatering.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.iotwatering.dto.SensorData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@Slf4j
public class SensorDataMapper {
	public SensorData mapResponseToSensorData(String jsonResponse) {
		SensorData sensorData = new SensorData();
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode root = mapper.readTree(jsonResponse);

			// Lấy giá trị Temperature
			if (root.has("temperature") && root.get("temperature").isArray() && !root.get("temperature").isEmpty()) {
				JsonNode tempNode = root.get("temperature").get(0);
				sensorData.setTemperature(Integer.parseInt(tempNode.get("value").asText()));
			}

			// Lấy giá trị Humidity
			if (root.has("humidity") && root.get("humidity").isArray() && !root.get("humidity").isEmpty()) {
				JsonNode humNode = root.get("humidity").get(0);
				sensorData.setHumidity(humNode.get("value").asText());
			}

			// Lấy giá trị Light
			if (root.has("light") && root.get("light").isArray() && !root.get("light").isEmpty()) {
				JsonNode lightNode = root.get("light").get(0);
				sensorData.setLight(lightNode.get("value").asText());
			}

			// Lấy giá trị Soil -> ánh xạ sang soilMoisture
			if (root.has("soil") && root.get("soil").isArray() && !root.get("soil").isEmpty()) {
				JsonNode soilNode = root.get("soil").get(0);
				sensorData.setSoilMoisture(soilNode.get("value").asText());
			}

			// Xác định thời gian đo (measuredTime) dựa trên timestamp lớn nhất trong các sensor
			long maxTs = 0;
			if (root.has("temperature") && root.get("temperature").isArray() && !root.get("temperature").isEmpty()) {
				maxTs = Math.max(maxTs, root.get("temperature").get(0).get("ts").asLong());
			}
			if (root.has("humidity") && root.get("humidity").isArray() && !root.get("humidity").isEmpty()) {
				maxTs = Math.max(maxTs, root.get("humidity").get(0).get("ts").asLong());
			}
			if (root.has("light") && root.get("light").isArray() && !root.get("light").isEmpty()) {
				maxTs = Math.max(maxTs, root.get("light").get(0).get("ts").asLong());
			}
			if (root.has("soil") && root.get("soil").isArray() && !root.get("soil").isEmpty()) {
				maxTs = Math.max(maxTs, root.get("soil").get(0).get("ts").asLong());
			}
			if (maxTs > 0) {
				LocalDateTime measuredTime = Instant.ofEpochMilli(maxTs)
						.atZone(ZoneId.systemDefault())
						.toLocalDateTime();
				sensorData.setMeasuredTime(measuredTime);
			}
		} catch (Exception e) {
			log.error("Error when mapping response to SensorData: {}", e.getMessage());
		}
		return sensorData;
	}
}
