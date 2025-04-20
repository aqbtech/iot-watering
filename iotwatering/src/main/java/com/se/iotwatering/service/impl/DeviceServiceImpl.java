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

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceServiceImpl implements DeviceService {

	private final SensorRepository sensorRepository;
	private final Device2Sensor device2Sensor;
	private final UserRepository userRepository;
	private final CoreIotDeviceAttribute coreIotDeviceAttribute;

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
		if (currentUserName == null) throw new WebServerException(AuthErrorCode.UNAUTHENTICATED);
		User user = userRepository.findByUsername(currentUserName)
				.orElseThrow(() -> new WebServerException(UserErrorCode.USER_NOT_FOUND));
		List<User> users = List.of(user);

		// add user to sensor
		sensor.setUsers(users);

		sensorRepository.save(sensor);
		// TODO: may be add sensor to user? or jpa will do it automatically?
		log.info("Added new device with ID: {}", request.getCoreIotDeviceId());

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
		return updateDeviceComponentState(request.getDeviceId(), "light", request.isState());
	}

	@Override
	@Transactional
	public boolean controlPump(DeviceStateRequest request) {
		return updateDeviceComponentState(request.getDeviceId(), "pump", request.isState());
	}

	@Override
	@Transactional
	public boolean controlSiren(DeviceStateRequest request) {
		return updateDeviceComponentState(request.getDeviceId(), "siren", request.isState());
	}

	@Override
	@Transactional
	public boolean controlFan(DeviceStateRequest request) {
		return updateDeviceComponentState(request.getDeviceId(), "fan", request.isState());
	}

	@Override
	public DeviceInfoResponse getDeviceInfo(String deviceId) {
		// Find the sensor by device ID
		Sensor sensor = sensorRepository.findByPureSensorId(deviceId)
				.orElseThrow(() -> new WebServerException(DeviceErrorCode.DEVICE_NOT_FOUND));

		Configuration config = sensor.getConfiguration();
		if (config == null) throw new WebServerException(DeviceErrorCode.CONFIG_NOT_FOUND);


		// Build response using the configuration getters
		return DeviceInfoResponse.builder()
				.name(sensor.getName())
				.location(sensor.getLocation())
				.fan(getComponentStatus(sensor, "fan"))
				.pump(getComponentStatus(sensor, "pump"))
				.siren(getComponentStatus(sensor, "siren"))
				.light(getComponentStatus(sensor, "light"))
				// Use actual configuration values instead of defaults
				.configFan(config.getHumidity())
				.configLight(config.getLight())
				.configSiren(config.getTemperature())
				.configPump(config.getSoilMoisture())
				.build();
	}

	/**
	 * Helper method to update the state of a device component
	 *
	 * @param deviceId  The device ID
	 * @param component The component name (light, pump, siren, fan)
	 * @param state     The new state (true = active, false = inactive)
	 * @return True if the update was successful
	 */
	private boolean updateDeviceComponentState(String deviceId, String component, boolean state) {
		try {
			// Find the sensor by device ID
			Sensor sensor = sensorRepository.findByPureSensorId(deviceId)
					.orElseThrow(() -> new WebServerException(DeviceErrorCode.DEVICE_NOT_FOUND));

			String coreiot_dvc_id = sensor.getPureSensorId();
			CoreIotDefaultKey key = CoreIotDefaultKey.resolve(component.toLowerCase());
			boolean response = coreIotDeviceAttribute.triggerAttribute(coreiot_dvc_id, key);
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
	private String getComponentStatus(Sensor sensor, String component) {
		String coreiot_dvc_id = sensor.getPureSensorId();
		CoreIotDefaultKey key = CoreIotDefaultKey.resolve(component.toLowerCase());
		JsonNode nowState = coreIotDeviceAttribute.getNowState(coreiot_dvc_id, key);
		return nowState.get("value").asText();
	}
}
