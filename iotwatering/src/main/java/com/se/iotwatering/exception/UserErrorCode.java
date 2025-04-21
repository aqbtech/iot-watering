package com.se.iotwatering.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND(2000, "User not found", HttpStatus.NOT_FOUND),
    USER_NOT_EXIST(2001, "User not found", HttpStatus.BAD_REQUEST),
    USER_EXISTED(2002, "Username already exists", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    UserErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
