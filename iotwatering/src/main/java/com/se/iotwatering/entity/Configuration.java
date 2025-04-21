package com.se.iotwatering.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Builder.Default
	private boolean autoControlEnabled = true; // true: hệ thống tự động điều khiển, false: user tự điều khiển
	private double soilMoisture;
	private double humidity;
	private double light;
	private double temperature;

	@OneToOne(mappedBy = "configuration", cascade = CascadeType.ALL)
	private Sensor sensor;
}
