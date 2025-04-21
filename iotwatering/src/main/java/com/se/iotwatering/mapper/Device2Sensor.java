package com.se.iotwatering.mapper;

import com.se.iotwatering.dto.http.request.DeviceAddRequest;
import com.se.iotwatering.entity.Sensor;

public interface Device2Sensor {
	/**
	 * Mapping add device request to sensor entity
	 * @param device The device_add request
	 * @return The sensor entity
	 */
	Sensor toSensor(DeviceAddRequest device);
}
