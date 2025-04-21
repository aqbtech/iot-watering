package com.se.iotwatering.dto.http.request;

import lombok.Data;

@Data
public class AlertConfig {
	private long deviceId;
	private boolean status;
}
