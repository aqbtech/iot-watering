package com.se.iotwatering.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {
	@Id @GeneratedValue
	private long notificationId;
	private String message;
	private LocalDate timeSent;

	@OneToMany(mappedBy = "notification")
	private List<ErrorLog> errorLogs = new ArrayList<>();
}
