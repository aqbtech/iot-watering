package com.se.iotwatering.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceInfo {
	private long deviceId;
	@JsonProperty("name")
	private String deviceName;
	private String location;
	private String status;
	private String temperature;
	private String humidity;
	private String light;
	private String soilMoisture;
}
