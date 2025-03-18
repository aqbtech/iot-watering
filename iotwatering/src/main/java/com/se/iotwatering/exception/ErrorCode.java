package com.se.iotwatering.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
	UNKNOWN_ERROR(5000, "Unknown error", HttpStatus.INTERNAL_SERVER_ERROR),
	INVALID_KEY(1000, "Invalid key", HttpStatus.BAD_REQUEST),
	UNAUTHENTICATED(1001, "Unauthenticated", HttpStatus.UNAUTHORIZED),
	UNAUTHORIZED(1002, "You do not have permission", HttpStatus.FORBIDDEN),
	USER_NOT_FOUND(1003, "User not found", HttpStatus.NOT_FOUND),
	USER_NOT_EXIST(1007, "User not found", HttpStatus.BAD_REQUEST),
	USER_EXISTED(1008, "Username existed", HttpStatus.BAD_REQUEST),
	DEVICE_NOT_FOUND(1004, "Device not found", HttpStatus.NOT_FOUND),;
	private final int code;
	private final String message;
	private final HttpStatusCode httpStatusCode;

	ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
		this.code = code;
		this.message = message;
		this.httpStatusCode = httpStatusCode;
	}

}
