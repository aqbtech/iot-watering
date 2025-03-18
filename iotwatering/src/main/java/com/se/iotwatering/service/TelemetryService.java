package com.se.iotwatering.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.iotwatering.config.CoreIotConfig;
import com.se.iotwatering.dto.SensorData;
import com.se.iotwatering.entity.Sensor;
import com.se.iotwatering.exception.ErrorCode;
import com.se.iotwatering.exception.WebServerException;
import com.se.iotwatering.mapper.SensorDataMapper;
import com.se.iotwatering.repo.SensorRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryService {
	private final SensorRepo sensorRepo;
	private final CoreIotRestClient coreIotRestClient;
	private final CoreIotConfig coreIotConfig;
	private final SensorDataMapper sensorDataMapper;

	public SensorData getLatestTelemetry(long deviceId) {
		Sensor device = sensorRepo.findById(deviceId)
				.orElseThrow(() -> new WebServerException(ErrorCode.DEVICE_NOT_FOUND));
		String coreDeviceId = device.getPureSensorId();
		// call to coreiot with endpoint /api/plugins/telemetry/DEVICE/{entityId}/values/timeseries?keys={list of att}&useStrictDataTypes=false
		String uri = "https://"+ coreIotConfig.getConfig().get("url") + "/api/plugins/telemetry/DEVICE/" + coreDeviceId + "/values/timeseries";
		Map<String, Object> queryParams = new LinkedHashMap<>();
		queryParams.put("keys", "temperature,humidity,light,soil");
		queryParams.put("useStrictDataTypes", "false");

		String res = coreIotRestClient.sendRequest("GET", uri, queryParams, null);
		log.info("Response from coreiot: {}", res);
		return sensorDataMapper.mapResponseToSensorData(res);
	}

	public Page<SensorData> getHistory(long deviceId, String from, String to, int page, int size) {
		return null;
	}
}
