package com.se.iotwatering.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
	private String name;
	private String location;
	private String status;
	@OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SensorData> dataRecords = new ArrayList<>();
}
