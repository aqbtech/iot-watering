package com.se.iotwatering.service;

import com.se.iotwatering.entity.Sensor;
import com.se.iotwatering.exception.ErrorCode;
import com.se.iotwatering.exception.WebServerException;
import com.se.iotwatering.repo.SensorRepo;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Component
public class DeviceStartupService {
    private final SensorRepo deviceRepository;
    private final WebSocketClient webSocketService;

    public DeviceStartupService(SensorRepo deviceRepository, WebSocketClient webSocketService) {
        this.deviceRepository = deviceRepository;
        this.webSocketService = webSocketService;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = true)
    public void onApplicationReady() {
        System.out.println("Ứng dụng đã khởi động! Truy vấn database và subscribe toàn bộ thiết bị...");

        // Truy vấn danh sách thiết bị từ database
        List<Sensor> devices = deviceRepository.findAll();
        System.out.println("Tìm thấy " + devices.size() + " thiết bị trong database.");

        System.out.println("Subscribing device: " + "2b1ab270-f29a-11ef-87b5-21bccf7d29d5");
        try {
            webSocketService.subscribeToDevice("2b1ab270-f29a-11ef-87b5-21bccf7d29d5");
        } catch (IOException e) {
            throw new WebServerException(ErrorCode.UNKNOWN_ERROR);
        }

        // Subscribe từng thiết bị vào WebSocket
//		devices.forEach(device -> {
//			System.out.println("Subscribing device: " + device.getName());
//			try {
//				webSocketService.subscribeToDevice(device.getPureSensorId());
//			} catch (IOException e) {
//				throw new WebServerException(ErrorCode.UNKNOWN_ERROR);
//			}
//		});

        System.out.println("Tất cả thiết bị đã được subscribe thành công.");
    }
}

