package com.se.iotwatering.dto;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class LoginResponse implements HttpResponseBasic  {
	private String token;

	public LoginResponse(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public ResponseEntity<?> getResponse() {
		return ResponseEntity.ok(this);
	}
}
