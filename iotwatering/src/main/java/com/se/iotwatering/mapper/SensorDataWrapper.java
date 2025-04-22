package com.se.iotwatering.mapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SensorDataWrapper {
	public List<List<Object>> humidity;
	public List<List<Object>> light;
	public List<List<Object>> temperature;
	public List<List<Object>> soil;
}
