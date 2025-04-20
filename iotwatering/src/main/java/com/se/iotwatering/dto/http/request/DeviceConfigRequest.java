package com.se.iotwatering.dto.http.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceConfigRequest {
    private String deviceId;
    private double temperature;
    private double humidity;
    private double soilMoisture;
    private double light;
}
