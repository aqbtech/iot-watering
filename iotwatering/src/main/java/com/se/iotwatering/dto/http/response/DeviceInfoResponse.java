package com.se.iotwatering.dto.http.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfoResponse {
    private String name;
    private String location;
    private String fan;
    private String pump;
    private String siren;
    private String light;
    private double configFan;
    private double configLight;
    private double configSiren;
    private double configPump;
}
