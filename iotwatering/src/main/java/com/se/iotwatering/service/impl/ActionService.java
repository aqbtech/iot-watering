package com.se.iotwatering.service.impl;

import com.se.iotwatering.entity.Sensor;
import com.se.iotwatering.dto.http.request.DeviceStateRequest;
import com.se.iotwatering.entity.User;
import com.se.iotwatering.exception.DeviceErrorCode;
import com.se.iotwatering.exception.WebServerException;
import com.se.iotwatering.repo.SensorRepository;
import com.se.iotwatering.service.DeviceService;
import com.se.iotwatering.entity.AlertState;
import com.se.iotwatering.repository.AlertStateRepository;
import com.se.iotwatering.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActionService {
    private final DeviceService deviceService;
    private final AlertStateRepository alertStateRepository;
    private final EmailService emailService;
    private final SensorRepository sensorRepository;

    public void sendEmail(String coreIotDeviceId, String htmlContent) {
        // fetch user email from the database
        Sensor sensor = sensorRepository.findWithUsersByPureSensorId(coreIotDeviceId)
                .orElseThrow(() -> new WebServerException(DeviceErrorCode.DEVICE_NOT_FOUND));
        String subject = "Alert from your sensor: " + coreIotDeviceId;
        User user = sensor.getUsers().getFirst();
        String to = user.getEmail();
        String toName = user.getFirstName() + " " + user.getLastName();
        emailService.sendEmail(to, subject, htmlContent, toName);
    }

    public void sendEmailOnce(String sensorId, String field, String message) {
        AlertState alertState = alertStateRepository.findBySensorIdAndField(sensorId, field)
                .orElse(AlertState.builder().sensorId(sensorId).field(field).alerted(false).build());
        if (!alertState.isAlerted()) {
            sendEmail(sensorId, message);
            alertState.setAlerted(true);
            alertStateRepository.save(alertState);
        }
    }

    public void resetEmailAlert(String sensorId, String field) {
        alertStateRepository.findBySensorIdAndField(sensorId, field).ifPresent(alertState -> {
            if (alertState.isAlerted()) {
                alertState.setAlerted(false);
                alertStateRepository.save(alertState);
            }
        });
    }

    public void turnOnFan(Sensor sensor) {

        deviceService.controlFan(new DeviceStateRequest(sensor.getSensorId(), true));
    }
    public void turnOffFan(Sensor sensor) {
        deviceService.controlFan(new DeviceStateRequest(sensor.getSensorId(), false));
    }
    public void turnOnPump(Sensor sensor) {
        deviceService.controlPump(new DeviceStateRequest(sensor.getSensorId(), true));
    }
    public void turnOffPump(Sensor sensor) {
        deviceService.controlPump(new DeviceStateRequest(sensor.getSensorId(), false));
    }
    public void turnOnLight(Sensor sensor) {
        deviceService.controlLight(new DeviceStateRequest(sensor.getSensorId(), true));
    }
    public void turnOffLight(Sensor sensor) {
        deviceService.controlLight(new DeviceStateRequest(sensor.getSensorId(), false));
    }
}


