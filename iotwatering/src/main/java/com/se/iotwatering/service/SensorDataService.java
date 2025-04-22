package com.se.iotwatering.service;

import com.se.iotwatering.entity.SensorData;
import com.se.iotwatering.entity.Configuration;
import com.se.iotwatering.entity.Sensor;
import com.se.iotwatering.exception.DeviceErrorCode;
import com.se.iotwatering.exception.WebServerException;
import com.se.iotwatering.repo.ConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SensorDataService {
    private final ThresholdEvaluator thresholdEvaluator;
    private final ConfigurationRepository configRepo;

    public void handleSensorData(SensorData data) {
        Configuration config = configRepo.findBySensor(data.getSensor())
                .orElseThrow(() -> new WebServerException(DeviceErrorCode.CONFIG_NOT_FOUND));
        thresholdEvaluator.evaluate(data, config);
    }
}
