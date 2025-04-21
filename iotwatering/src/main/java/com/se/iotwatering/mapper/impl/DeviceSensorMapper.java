package com.se.iotwatering.mapper.impl;

import com.se.iotwatering.dto.http.request.DeviceAddRequest;
import com.se.iotwatering.entity.Sensor;
import com.se.iotwatering.mapper.Device2Sensor;
import org.springframework.stereotype.Component;

@Component
public class DeviceSensorMapper implements Device2Sensor {
	@Override
	public Sensor toSensor(DeviceAddRequest device) {
		Sensor sensor = new Sensor();
		sensor.setPureSensorId(device.getCoreIotDeviceId());
		sensor.setName(device.getDeviceName());
		sensor.setLocation(device.getDeviceLocation());
		sensor.setStatus(device.getDeviceStatus());
		return sensor;
	}

}
