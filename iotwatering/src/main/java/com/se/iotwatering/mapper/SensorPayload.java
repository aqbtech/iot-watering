package com.se.iotwatering.mapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SensorPayload {
	public int subscriptionId;
	public int errorCode;
	public String errorMsg;
	public SensorDataWrapper data;
	public Map<String, Long> latestValues;
}
