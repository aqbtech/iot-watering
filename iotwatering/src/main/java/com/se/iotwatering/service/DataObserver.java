package com.se.iotwatering.service;

public interface DataObserver {
	void onMessage(String entityId, String payload);
}
