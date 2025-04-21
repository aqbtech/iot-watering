package com.se.iotwatering.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.se.iotwatering.constant.ConfigurationDefault;
import com.se.iotwatering.constant.CoreIotDefaultKey;
import com.se.iotwatering.dto.http.request.DeviceAddRequest;
import com.se.iotwatering.dto.http.request.DeviceConfigRequest;
import com.se.iotwatering.dto.http.request.DeviceStateRequest;
import com.se.iotwatering.dto.http.response.DeviceInfoResponse;
import com.se.iotwatering.entity.Configuration;
import com.se.iotwatering.entity.Sensor;
import com.se.iotwatering.entity.User;
import com.se.iotwatering.exception.AuthErrorCode;
import com.se.iotwatering.exception.DeviceErrorCode;
import com.se.iotwatering.exception.UserErrorCode;
import com.se.iotwatering.exception.WebServerException;
import com.se.iotwatering.mapper.Device2Sensor;
import com.se.iotwatering.repo.SensorRepository;
import com.se.iotwatering.repo.UserRepository;
import com.se.iotwatering.service.CoreIotDeviceAttribute;
import com.se.iotwatering.service.DeviceService;
import com.se.iotwatering.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.socket.server.WebSocketService;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceServiceImpl implements DeviceService {

	private final SensorRepository sensorRepository;
	private final Device2Sensor device2Sensor;
	private final UserRepository userRepository;
	private final CoreIotDeviceAttribute coreIotDeviceAttribute;
	private final WebSocketClient webSocketService;

	@Override
	@Transactional
	public boolean addDevice(DeviceAddRequest request) {
		// TODO: unique in pure_sensor_id entity
		if (sensorRepository.existsByPureSensorId(request.getCoreIotDeviceId())) {
			log.warn("Device with ID {} already exists", request.getCoreIotDeviceId());
			return false;
		}

		Sensor sensor = device2Sensor.toSensor(request);

		// Create default configuration
		Configuration configuration = Configuration.builder()
				.humidity(ConfigurationDefault.DEFAULT_HUMIDITY)
				.light(ConfigurationDefault.DEFAULT_LIGHT)
				.soilMoisture(ConfigurationDefault.DEFAULT_SOIL_MOISTURE)
				.temperature(ConfigurationDefault.DEFAULT_TEMPERATURE)
				.build();

		sensor.setConfiguration(configuration);

		// assign current user for this device(sensor)
		String currentUserName = SecurityUtil.getCurrentUsername();

		User user = userRepository.findByUsername(currentUserName)
				.orElseThrow(() -> new WebServerException(UserErrorCode.USER_NOT_FOUND));
		List<User> users = List.of(user);

		// add user to sensor
		sensor.setUsers(users);

		sensorRepository.save(sensor);
		// TODO: may be add sensor to user? or jpa will do it automatically?
		log.info("Added new device with ID: {}", request.getCoreIotDeviceId());
		// subscribe to device
		try {
			webSocketService.subscribeToDevice(request.getCoreIotDeviceId());
		} catch (IOException e) {
			throw new WebServerException(DeviceErrorCode.CAN_NOT_SUBSCRIBE_DEVICE);
		}
		return true;
	}

	@Override
	@Transactional
	public boolean setConfig(DeviceConfigRequest request) {
		Sensor sensor = sensorRepository.findByPureSensorId(request.getDeviceId())
				.orElseThrow(() -> new WebServerException(DeviceErrorCode.DEVICE_NOT_FOUND));

		Configuration configuration = sensor.getConfiguration();
		boolean flag = configuration != null;
		Configuration newConfig = Configuration.builder()
				.temperature(
						flag
								? configuration.getTemperature()
								: (int) request.getTemperature()
				)
				.humidity(
						flag
								? configuration.getHumidity()
								: request.getHumidity()
				)
				.soilMoisture(
						flag
								? configuration.getSoilMoisture()
								: request.getSoilMoisture()
				)
				.light(
						flag
								? configuration.getLight()
								: request.getLight()
				)
				.build();

		sensor.setConfiguration(newConfig);

		sensorRepository.save(sensor);
		log.info("Updated configuration for device ID: {}", request.getDeviceId());

		return true;
	}

	@Override
	@Transactional
	public boolean controlLight(DeviceStateRequest request) {
		String deviceId = sensorRepository.findById(request.getDeviceId())
				.orElseThrow(() -> new WebServerException(DeviceErrorCode.DEVICE_NOT_FOUND)).getPureSensorId();
		return updateDeviceComponentState(deviceId, CoreIotDefaultKey.LIGHT_CONTROL, request.isState());
	}

	@Override
	@Transactional
	public boolean controlPump(DeviceStateRequest request) {
		String deviceId = sensorRepository.findById(request.getDeviceId())
				.orElseThrow(() -> new WebServerException(DeviceErrorCode.DEVICE_NOT_FOUND)).getPureSensorId();
		return updateDeviceComponentState(deviceId, CoreIotDefaultKey.PUMP_CONTROL, request.isState());
	}

	@Override
	@Transactional
	public boolean controlSiren(DeviceStateRequest request) {
		String deviceId = sensorRepository.findById(request.getDeviceId())
				.orElseThrow(() -> new WebServerException(DeviceErrorCode.DEVICE_NOT_FOUND)).getPureSensorId();
		return updateDeviceComponentState(deviceId, CoreIotDefaultKey.SIREN_CONTROL, request.isState());
	}

	@Override
	@Transactional
	public boolean controlFan(DeviceStateRequest request) {
		String deviceId = sensorRepository.findById(request.getDeviceId())
				.orElseThrow(() -> new WebServerException(DeviceErrorCode.DEVICE_NOT_FOUND)).getPureSensorId();
		return updateDeviceComponentState(deviceId, CoreIotDefaultKey.FAN_CONTROL, request.isState());
	}
	@Override
	public DeviceInfoResponse getDeviceDetail(Long deviceId) {
		// Find the sensor by device ID
		Sensor sensor = sensorRepository.findById(deviceId)
				.orElseThrow(() -> new WebServerException(DeviceErrorCode.DEVICE_NOT_FOUND));

		Configuration config = sensor.getConfiguration();
		if (config == null) throw new WebServerException(DeviceErrorCode.CONFIG_NOT_FOUND);


		// Build response using the configuration getters
		var result = DeviceInfoResponse.builder()
				.name(sensor.getName())
				.location(sensor.getLocation())
				.fan(getComponentStatus(sensor, CoreIotDefaultKey.FAN_CONTROL))
				.pump(getComponentStatus(sensor, CoreIotDefaultKey.PUMP_CONTROL))
				.siren(getComponentStatus(sensor, CoreIotDefaultKey.SIREN_CONTROL))
				.light(getComponentStatus(sensor, CoreIotDefaultKey.LIGHT_CONTROL))
				// Use actual configuration values instead of defaults
				.configFan(config.getHumidity())
				.configLight(config.getLight())
				.configSiren(config.getTemperature())
				.configPump(config.getSoilMoisture())
				.build();
		return result;
	}


	@Override
	public DeviceInfoResponse getDeviceInfo(String deviceId) {
		// Find the sensor by device ID
		Sensor sensor = sensorRepository.findByPureSensorId(deviceId)
				.orElseThrow(() -> new WebServerException(DeviceErrorCode.DEVICE_NOT_FOUND));

		Configuration config = sensor.getConfiguration();
		if (config == null) throw new WebServerException(DeviceErrorCode.CONFIG_NOT_FOUND);


		// Build response using the configuration getters
		var result =  DeviceInfoResponse.builder()
				.name(sensor.getName())
				.location(sensor.getLocation())
				.fan(getComponentStatus(sensor, CoreIotDefaultKey.FAN_CONTROL))
				.pump(getComponentStatus(sensor, CoreIotDefaultKey.PUMP_CONTROL))
				.siren(getComponentStatus(sensor, CoreIotDefaultKey.SIREN_CONTROL))
				.light(getComponentStatus(sensor, CoreIotDefaultKey.LIGHT_CONTROL))
				// Use actual configuration values instead of defaults
				.configFan(config.getHumidity())
				.configLight(config.getLight())
				.configSiren(config.getTemperature())
				.configPump(config.getSoilMoisture())
				.build();
		return result;
	}

	/**
	 * Helper method to update the state of a device component
	 *
	 * @param deviceId  The device ID
	 * @param component The component name (light, pump, siren, fan)
	 * @param state     The new state (true = active, false = inactive)
	 * @return True if the update was successful
	 */
	private boolean updateDeviceComponentState(String deviceId, CoreIotDefaultKey component, boolean state) {
		// Find the sensor by device ID
		Sensor sensor = sensorRepository.findByPureSensorId(deviceId)
				.orElseThrow(() -> new WebServerException(DeviceErrorCode.DEVICE_NOT_FOUND));
		try {
			String coreiot_dvc_id = sensor.getPureSensorId();
//			CoreIotDefaultKey key = CoreIotDefaultKey.resolve(component.toLowerCase());
			boolean response = coreIotDeviceAttribute.triggerAttribute(coreiot_dvc_id, component);
			if (!response) {
				log.error("Failed to trigger {} for device ID: {}", component, deviceId);
				return false;
			}
			log.info("Updated {} state to {} for device ID: {}", component, state ? "active" : "inactive", deviceId);

			return true;
		} catch (Exception e) {
			log.error("Error updating {} state for device ID: {}", component, deviceId, e);
			return false;
		}
	}

	/**
	 * Helper method to get the status of a component
	 *
	 * @param sensor    The sensor entity
	 * @param component The component name
	 * @return The component status ("active" or "inactive")
	 */
	private String getComponentStatus(Sensor sensor, CoreIotDefaultKey component) {
		String coreiot_dvc_id = sensor.getPureSensorId();
//		CoreIotDefaultKey key = CoreIotDefaultKey.resolve(component.toLowerCase());
		JsonNode nowState = coreIotDeviceAttribute.getNowState(coreiot_dvc_id, component);
		return nowState.get("value").asText();
	}
}
