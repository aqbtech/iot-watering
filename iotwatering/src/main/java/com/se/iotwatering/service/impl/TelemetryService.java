package com.se.iotwatering.service.impl;

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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
		String uri = "https://" + coreIotConfig.getConfig().get("url") + "/api/plugins/telemetry/DEVICE/" + coreDeviceId + "/values/timeseries";
		Map<String, Object> queryParams = new LinkedHashMap<>();
		queryParams.put("keys", "temperature,humidity,light,soil");
		queryParams.put("useStrictDataTypes", "false");

		String res = coreIotRestClient.sendRequest("GET", uri, queryParams, null);
		log.info("Response from coreiot: {}", res);
		return sensorDataMapper.mapResponseToSensorData(res);
	}

	public Page<SensorData> getHistory(long deviceId, String from, String to, int page, int size) {
		Sensor device = sensorRepo.findById(deviceId)
				.orElseThrow(() -> new WebServerException(ErrorCode.DEVICE_NOT_FOUND));
		String coreDeviceId = device.getPureSensorId();
		// call to coreiot with endpoint /api/plugins/telemetry/DEVICE/2b1ab270-f29a-11ef-87b5-21bccf7d29d5/values/timeseries?keys=temperature%2Chumidity%2Clight%2Csoil&startTs=1609459200000&endTs=1742291975323&limit=100&useStrictDataTypes=false
		String uri = "https://" + coreIotConfig.getConfig().get("url") + "/api/plugins/telemetry/DEVICE/" + coreDeviceId + "/values/timeseries";
		Map<String, Object> queryParams = new LinkedHashMap<>();
		queryParams.put("keys", "temperature,humidity,light,soil");
		queryParams.put("startTs", from);
		queryParams.put("endTs", to);
		queryParams.put("limit", size);
		queryParams.put("useStrictDataTypes", "false");

		String res = coreIotRestClient.sendRequest("GET", uri, queryParams, null);
		log.info("Response from coreiot: {}", res);
//		return PaginationUtils.convertListToPage();
		Pageable pageable = Pageable.ofSize(size).withPage(page);
		return sensorDataMapper.mapResponseToSensorDataPage(res, pageable);
	}
}
