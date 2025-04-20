package com.se.iotwatering.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.iotwatering.config.CoreIotConfig;
import com.se.iotwatering.constant.CoreIotDefaultKey;
import com.se.iotwatering.dto.DeviceInfo;
import com.se.iotwatering.dto.SensorData;
import com.se.iotwatering.dto.SensorDetailResponse;
import com.se.iotwatering.entity.Sensor;
import com.se.iotwatering.exception.DeviceErrorCode;
import com.se.iotwatering.exception.WebServerException;
import com.se.iotwatering.repo.SensorRepo;
import com.se.iotwatering.service.CoreIotDeviceAttribute;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeviceControllerService implements CoreIotDeviceAttribute {
	private final CoreIotRestClient coreIotRestClient;
	private final SensorRepo sensorRepo;
	private final TelemetryService telemetryService;
	private final CoreIotConfig coreIotConfig;

	public JsonNode getNowState(String deviceId, CoreIotDefaultKey attribute) {
		String uri = "https://app.coreiot.io/api/plugins/telemetry/DEVICE/" + deviceId + "/values/attributes";
		Map<String, Object> queryParams = new LinkedHashMap<>();
		queryParams.put("keys", attribute.getKey());
		String nowState = coreIotRestClient.sendRequest("GET", uri, queryParams, null);
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode root = mapper.readTree(nowState);
			if (root.isArray() && !root.isEmpty()) {
				JsonNode attributeNode = root.get(root.size() - 1);
				return attributeNode;
			} else {
				throw new WebServerException(DeviceErrorCode.FIELD_NOT_FOUND);
			}
		} catch (Exception e) {
			throw new WebServerException(DeviceErrorCode.DEVICE_NOT_FOUND);
		}
	}
	
	@Override
	public String changeAttribute(String deviceId, CoreIotDefaultKey attribute, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean triggerAttribute(String deviceId, CoreIotDefaultKey attribute) {
//		String uri = "https://app.coreiot.io/api/plugins/telemetry/DEVICE/2b1ab270-f29a-11ef-87b5-21bccf7d29d5/attributes/SHARED_SCOPE";
		String uri = "https://" + coreIotConfig.getConfig().get("url") + "/api/plugins/telemetry/DEVICE/" + deviceId + "/SHARED_SCOPE";
		JsonNode nowState = getNowState(deviceId, attribute);
		String result;
		if (nowState.get("value").asInt() == 1) {
			Map<String, Object> body = Map.of(attribute.getKey(), 0);
			result = coreIotRestClient.sendRequest("POST", uri, null, body);
		} else {
			Map<String, Object> body = Map.of(attribute.getKey(), 1);
			result = coreIotRestClient.sendRequest("POST", uri, null, body);
		}
		// check new now state to ensure the change is successful
		JsonNode newState = getNowState(deviceId, attribute);
		return newState.get("value").asInt() == (result.equals("1") ? 1 : 0);
	}
	
	public String triggerPump(long deviceId) {
		Sensor device = sensorRepo.findById(deviceId)
				.orElseThrow(() -> new WebServerException(DeviceErrorCode.DEVICE_NOT_FOUND));
//		String uri = "https://app.coreiot.io/api/plugins/telemetry/DEVICE/2b1ab270-f29a-11ef-87b5-21bccf7d29d5/attributes/SHARED_SCOPE";
		String uri = "https://" + coreIotConfig.getConfig().get("url") + "/api/plugins/telemetry/DEVICE/" + device.getPureSensorId() + "/SHARED_SCOPE";
		String nuri = "https://" + coreIotConfig.getConfig().get("url") + "/api/plugins/telemetry/DEVICE/" + device.getPureSensorId() + "/values/attributes";
		Map<String, Object> queryParams = new LinkedHashMap<>();
		queryParams.put("keys", "fanControl");
		String nowState =  coreIotRestClient.sendRequest("GET", nuri, queryParams, null);
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode root = mapper.readTree(nowState);
			// sample response: [
			//  {
			//    "lastUpdateTs": 1742319616081,
			//    "key": "booleanKey",
			//    "value": 1
			//  }
			//]
			if (root.isArray() && !root.isEmpty()) {
				JsonNode fanControlNode = root.get(root.size() -1);
				if (fanControlNode.get("value").asInt() == 1) {
					Map<String, Object> body = Map.of("fanControl", 0);
					return coreIotRestClient.sendRequest("POST", uri, null, body);
				}
			}
			Object body = Map.of("fanControl", 1);
			return coreIotRestClient.sendRequest("POST", uri, null, body);
		} catch (Exception e) {
			throw new WebServerException(DeviceErrorCode.DEVICE_NOT_FOUND);
		}
	}

	public SensorDetailResponse getDeviceById(long deviceId) {
		Sensor sensor = sensorRepo.findById(deviceId)
				.orElseThrow(() -> new WebServerException(DeviceErrorCode.DEVICE_NOT_FOUND));
		String nuri = "https://" + coreIotConfig.getConfig().get("url") + "/api/plugins/telemetry/DEVICE/" + sensor.getPureSensorId() + "/values/attributes";
		Map<String, Object> queryParams = new LinkedHashMap<>();
		queryParams.put("keys", "fanControl");
		String nowState = coreIotRestClient.sendRequest("GET", nuri, queryParams, null);
		SensorDetailResponse sensorDetailResponse = new SensorDetailResponse();
		sensorDetailResponse.setName(sensor.getName());
		sensorDetailResponse.setLocation(sensor.getLocation());
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode root = mapper.readTree(nowState);
			if (root.isArray() && !root.isEmpty()) {
				JsonNode fanControlNode = root.get(root.size() - 1);
				int fanControlValue = fanControlNode.get("value").asInt(); // Lấy giá trị "value"
				if (fanControlValue == 1) {
					sensorDetailResponse.setStatus("active"); // Chuyển về String nếu cần
				} else {
					sensorDetailResponse.setStatus("inactive");
				}
			}
		} catch (Exception e) {
			throw new WebServerException(DeviceErrorCode.DEVICE_NOT_FOUND);
		}
		return sensorDetailResponse;
	}

	public Page<DeviceInfo> listDevice(int page, int size) {
		Page<Sensor> sensors = sensorRepo.findAll(Pageable.ofSize(size).withPage(page));
		return sensors.map(sensor -> {
			SensorData sensorData = telemetryService.getLatestTelemetry(sensor.getSensorId());
			return new DeviceInfo(
					sensor.getSensorId(),
					sensor.getName(),
					sensor.getLocation(),
					sensor.getStatus(),
					sensorData.getTemperature(),
					sensorData.getHumidity(),
					sensorData.getLight(),
					sensorData.getSoilMoisture()
					);
		});
	}
}
