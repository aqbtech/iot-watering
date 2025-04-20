package com.se.iotwatering.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum DeviceErrorCode implements ErrorCode {
    DEVICE_NOT_FOUND(3000, "Device not found", HttpStatus.NOT_FOUND),
    FIELD_NOT_FOUND(3001, "Field not found", HttpStatus.NOT_FOUND),
    CONFIG_NOT_FOUND(3002, "Configuration of this device not found", HttpStatus.NOT_FOUND);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    DeviceErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
