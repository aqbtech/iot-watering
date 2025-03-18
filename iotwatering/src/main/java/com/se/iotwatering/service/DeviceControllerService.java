package com.se.iotwatering.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceControllerService {
	private final CoreIotRestClient coreIotRestClient;
	public String triggerPump() {
		return "Triggered pump";
	}
}
