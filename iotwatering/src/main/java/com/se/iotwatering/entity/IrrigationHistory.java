package com.se.iotwatering.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IrrigationHistory {
	@Id @GeneratedValue
	private long historyId;
	private LocalDate startTime;
	private LocalDate endTime;
	private int waterAmount;

	@ManyToOne
	@JoinColumn(name = "pump_id")
	private Pump pump;
}
