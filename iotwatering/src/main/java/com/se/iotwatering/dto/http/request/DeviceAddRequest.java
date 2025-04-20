package com.se.iotwatering.dto.http.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceAddRequest {
    @JsonProperty("device_id")
    private String coreIotDeviceId;
    private String deviceName;
    private String deviceLocation;
    private String deviceStatus;

}
