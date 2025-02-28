package com.se.iotwatering.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
	UNKNOWN_ERROR(5000, "Unknown error", HttpStatus.INTERNAL_SERVER_ERROR),
	FILE_SERVICE_ERROR(5001, "File service error", HttpStatus.INTERNAL_SERVER_ERROR),
	INVALID_KEY(1000, "Invalid key", HttpStatus.BAD_REQUEST),
	UNAUTHENTICATED(1001, "Unauthenticated", HttpStatus.UNAUTHORIZED),
	UNAUTHORIZED(1002, "You do not have permission", HttpStatus.FORBIDDEN),
	USER_NOT_FOUND(1003, "User not found", HttpStatus.NOT_FOUND),
	USER_NOT_EXIST(1007, "User not found", HttpStatus.BAD_REQUEST),
	USER_EXISTED(1008, "Username existed", HttpStatus.BAD_REQUEST),
	CART_NOT_FOUND(2000, "Cart not found", HttpStatus.NOT_FOUND),
	CART_PRODUCT_NOT_FOUND(2001, "Product not found in cart", HttpStatus.NOT_FOUND),
	PRODUCT_NOT_FOUND(1004, "Product not found", HttpStatus.NOT_FOUND),
	PRODUCT_NOT_BELONG_TO_SELLER(1005, "Product not belong to seller", HttpStatus.NOT_FOUND),
	CATEGORY_NOT_FOUND(1006, "Category not found", HttpStatus.NOT_FOUND),
	CATEGORY_HAS_CHILDREN(1007, "Category has children", HttpStatus.BAD_REQUEST),
	ORDER_NOT_FOUND(3001, "Order not found", HttpStatus.NOT_FOUND),
	DELIVERY_INFOR_NOT_FOUND(4001, "Delivery Information not found", HttpStatus.NOT_FOUND),
	DELIVERY_INFOR_NOT_EXIST(4002, "Delivery Information not exist", HttpStatus.BAD_REQUEST),
  	INSUFFICIENT_STOCK(2002, "Not enough in stock", HttpStatus.BAD_REQUEST),
	ADDRESS_NOT_FOUND(4003, "Address not found", HttpStatus.NOT_FOUND);
	private final int code;
	private final String message;
	private final HttpStatusCode httpStatusCode;

	ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
		this.code = code;
		this.message = message;
		this.httpStatusCode = httpStatusCode;
	}

}
