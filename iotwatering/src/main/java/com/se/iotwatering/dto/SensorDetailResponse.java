package com.se.iotwatering.dto;

import lombok.Data;

@Data
public class SensorDetailResponse {
    private String name;
    private String location;
    private String status;
}
