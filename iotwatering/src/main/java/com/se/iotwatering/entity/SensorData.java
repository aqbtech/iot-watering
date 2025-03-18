package com.se.iotwatering.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class SensorData {
	@Id @GeneratedValue
	private long dataId;
	private String soilMoisture;
	private String humidity;
	private String light;
	private int temperature;
	private LocalDateTime measuredTime;

	@ManyToOne
	@JoinColumn(name = "sensor_id", referencedColumnName = "sensorId")
	private Sensor sensor;
}
