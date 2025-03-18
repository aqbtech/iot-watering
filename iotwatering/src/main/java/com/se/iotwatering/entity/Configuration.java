package com.se.iotwatering.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String soilMoisture;
	private String humidity;
	private String light;
	private int temperature;

	@OneToOne(mappedBy = "configuration")
	private Sensor sensor;
}
