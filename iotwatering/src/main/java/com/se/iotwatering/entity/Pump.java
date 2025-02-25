package com.se.iotwatering.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pump {
	@Id
	private long pumpId;
	private String location;
	private String status;
	private LocalDate lastWatered;

	@OneToMany(mappedBy = "pump")
	private List<IrrigationHistory> irrigationHistories = new ArrayList<>();
}
