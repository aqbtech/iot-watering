package com.se.iotwatering.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SensorDataDTO {
    @JsonProperty("soilMoisture")
    private String soilMoisture;
    @JsonProperty("Humidity")
    private String humidity;
    @JsonProperty("Light")
    private String light;
    @JsonProperty("Temperature")
    private String temperature;
    @JsonProperty("updatedTime")
    private String measuredTime;
}
