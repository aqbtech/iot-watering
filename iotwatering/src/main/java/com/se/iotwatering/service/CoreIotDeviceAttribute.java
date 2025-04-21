package com.se.iotwatering.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.se.iotwatering.constant.CoreIotDefaultKey;

public interface CoreIotDeviceAttribute {
    String changeAttribute(String deviceId, CoreIotDefaultKey attribute, Object value);
    boolean triggerAttribute(String deviceId, CoreIotDefaultKey attribute);
    JsonNode getNowState(String deviceId, CoreIotDefaultKey attribute);
}
