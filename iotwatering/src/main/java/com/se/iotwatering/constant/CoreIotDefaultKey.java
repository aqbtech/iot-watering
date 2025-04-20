package com.se.iotwatering.constant;

public enum CoreIotDefaultKey {
    FAN_CONTROL("fanControl"),
    PUMP_CONTROL("pumpControl"),
    SIREN("fanState"),
    PUMP_STATE("pumpState");

    private final String key;

    CoreIotDefaultKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static CoreIotDefaultKey resolve(String key) {
        for (CoreIotDefaultKey k : values()) {
            if (k.key.equalsIgnoreCase(key)) {
                return k;
            }
        }
        throw new IllegalArgumentException("Unknown key: " + key);
    }
}