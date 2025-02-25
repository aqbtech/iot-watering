package com.se.iotwatering.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IrrigationSchedule {
	@Id @GeneratedValue
	private long scheduleId;
	private LocalDate startTime;
	private LocalDate endTime;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "pump_id")
	private User user;
}
