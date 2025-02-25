package com.se.iotwatering.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SensorData {
	@Id @GeneratedValue
	private long dataId;
	private String soilMoisture;
	private String humidity;
	private String light;
	private int temperature;

	@ManyToOne
	@JoinColumn(name = "sensor_id", referencedColumnName = "sensorId")
	private Sensor sensor;
}
