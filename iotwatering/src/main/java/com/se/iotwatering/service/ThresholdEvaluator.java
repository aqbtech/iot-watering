package com.se.iotwatering.service;

import com.se.iotwatering.entity.SensorData;
import com.se.iotwatering.entity.Configuration;

public interface ThresholdEvaluator {
    void evaluate(SensorData data, Configuration config);
}
