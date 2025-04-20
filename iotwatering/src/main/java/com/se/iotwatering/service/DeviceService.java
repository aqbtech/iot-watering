package com.se.iotwatering.service;

import com.se.iotwatering.dto.http.request.DeviceAddRequest;
import com.se.iotwatering.dto.http.request.DeviceConfigRequest;
import com.se.iotwatering.dto.http.request.DeviceStateRequest;
import com.se.iotwatering.dto.http.response.DeviceInfoResponse;

public interface DeviceService {
    /**
     * Add a new device to the system, and assign it for specific user
     * @param request Request containing the device ID
     * @return True if the device was added successfully
     */
//    boolean addDevice(String username, DeviceAddRequest request);
    boolean addDevice(DeviceAddRequest request);
    
    /**
     * Configure the environmental parameters for a device
     * @param request Request containing the configuration parameters
     * @return True if the configuration was updated successfully
     */
    boolean setConfig(DeviceConfigRequest request);
    
    /**
     * Control the light of a device
     * @param request Request containing the device ID and the state to set
     * @return True if the light state was updated successfully
     */
    boolean controlLight(DeviceStateRequest request);
    
    /**
     * Control the pump of a device
     * @param request Request containing the device ID and the state to set
     * @return True if the pump state was updated successfully
     */
    boolean controlPump(DeviceStateRequest request);
    
    /**
     * Control the siren of a device
     * @param request Request containing the device ID and the state to set
     * @return True if the siren state was updated successfully
     */
    boolean controlSiren(DeviceStateRequest request);
    
    /**
     * Control the fan of a device
     * @param request Request containing the device ID and the state to set
     * @return True if the fan state was updated successfully
     */
    boolean controlFan(DeviceStateRequest request);
    
    /**
     * Get information about a device
     * @param deviceId The ID of the device
     * @return Information about the device
     */
    DeviceInfoResponse getDeviceInfo(String deviceId);
}
