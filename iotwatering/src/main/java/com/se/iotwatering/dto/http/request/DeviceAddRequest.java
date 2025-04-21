package com.se.iotwatering.dto.http.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*; // hoặc import riêng các annotation

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceAddRequest {

    @NotBlank(message = "device_id không được để trống")
    @JsonProperty("device_id")
    private String coreIotDeviceId;

    @NotBlank(message = "deviceName không được để trống")
    private String deviceName;

    @NotBlank(message = "deviceLocation không được để trống")
    private String deviceLocation;

    @NotBlank(message = "deviceStatus không được để trống")
    private String deviceStatus;
}
