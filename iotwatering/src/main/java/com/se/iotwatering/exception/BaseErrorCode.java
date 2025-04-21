package com.se.iotwatering.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum BaseErrorCode implements ErrorCode {
    UNKNOWN_ERROR(5000, "Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    BaseErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
