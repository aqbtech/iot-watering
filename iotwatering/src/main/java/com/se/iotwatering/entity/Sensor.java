package com.se.iotwatering.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long sensorId;
	private String pureSensorId; // device id in core iot
	// core iot device access token
	private String name;
	private String location;
	private String status;
	@OneToOne(cascade = CascadeType.ALL)
	private Configuration configuration;
	@Builder.Default
	@OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SensorData> dataRecords = new ArrayList<>();
	@Builder.Default
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_sensor",
			joinColumns = @JoinColumn(name = "sensor_id", referencedColumnName = "sensorId"),
			inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "userId"))
	private List<User> users = new ArrayList<>();
}
