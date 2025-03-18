package com.se.iotwatering.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sensor {
	@Id
	private long sensorId;
	private String pureSensorId; // device id in core iot
	// core iot device access token
	private String name;
	private String location;
	private String status;
	@OneToOne
	private Configuration configuration;
	@OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SensorData> dataRecords = new ArrayList<>();
}
