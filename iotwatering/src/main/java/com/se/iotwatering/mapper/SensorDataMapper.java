package com.se.iotwatering.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.iotwatering.dto.SensorData;
import com.se.iotwatering.util.PaginationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

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
				sensorData.setTemperature(tempNode.get("value").asText());
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
	/**
	 * Chuyển từ response JSON sang Page<SensorData>.
	 * Các mảng "temperature", "humidity", "light" (và tùy chọn "soil") được giả sử là song song (cùng số lượng phần tử)
	 */
	public Page<SensorData> mapResponseToSensorDataPage(String jsonResponse, Pageable pageable) {
		List<SensorData> sensorDataList = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode root = mapper.readTree(jsonResponse);

			JsonNode tempArray = root.get("temperature");
			JsonNode humArray = root.get("humidity");
			JsonNode lightArray = root.get("light");
			JsonNode soilArray = root.get("soil");

			if (tempArray != null && tempArray.isArray()) {
				int length = tempArray.size();
				for (int i = 0; i < length; i++) {
					SensorData sensorData = new SensorData();

					JsonNode tempNode = tempArray.get(i);
					sensorData.setTemperature(tempNode.get("value").asText());
					long maxTs = tempNode.get("ts").asLong();

					if (humArray != null && humArray.size() > i) {
						JsonNode humNode = humArray.get(i);
						sensorData.setHumidity(humNode.get("value").asText());
						maxTs = Math.max(maxTs, humNode.get("ts").asLong());
					}

					if (lightArray != null && lightArray.size() > i) {
						JsonNode lightNode = lightArray.get(i);
						sensorData.setLight(lightNode.get("value").asText());
						maxTs = Math.max(maxTs, lightNode.get("ts").asLong());
					}

					if (soilArray != null && soilArray.size() > i) {
						JsonNode soilNode = soilArray.get(i);
						sensorData.setSoilMoisture(soilNode.get("value").asText());
						maxTs = Math.max(maxTs, soilNode.get("ts").asLong());
					}

					LocalDateTime measuredTime = Instant.ofEpochMilli(maxTs)
							.atZone(ZoneId.systemDefault())
							.toLocalDateTime();
					sensorData.setMeasuredTime(measuredTime);

					sensorDataList.add(sensorData);
				}
			}
		} catch (Exception e) {
			log.error("Error mapping response to SensorData Page: {}", e.getMessage());
		}
		return PaginationUtils.convertListToPage(sensorDataList, pageable);
	}
}
