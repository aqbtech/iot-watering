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
	private double soilMoisture;
	private double humidity;
	private double light;
	private double temperature;

	@OneToOne(mappedBy = "configuration", cascade = CascadeType.ALL)
	private Sensor sensor;
}
