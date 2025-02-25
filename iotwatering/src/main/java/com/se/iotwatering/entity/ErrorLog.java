package com.se.iotwatering.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorLog {
	@Id
	private long errorCode;
	private String errorMessage;

	@ManyToOne(optional = false)
	@JoinColumn(name = "notification_id")
	private Notification notification;
}
