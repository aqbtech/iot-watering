package com.se.iotwatering.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.iotwatering.exception.AuthErrorCode;
import com.se.iotwatering.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request,
						 HttpServletResponse response,
						 AuthenticationException authException)
			throws IOException, ServletException {

		ObjectMapper objectMapper = new ObjectMapper();
		if(authException.getMessage().equalsIgnoreCase("token is expired")) {
			ErrorCode errorCode = AuthErrorCode.TOKEN_EXPIRED;
			response.setStatus(errorCode.getHttpStatus().value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);

			ResponseEntity<?> apiResponse = ResponseEntity.status(errorCode.getHttpStatus())
				.body(errorCode.getMessage());
			response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
			response.flushBuffer();
		}
		ErrorCode errorCode = AuthErrorCode.UNAUTHENTICATED;

		response.setStatus(errorCode.getHttpStatus().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		ResponseEntity<?> apiResponse = ResponseEntity.status(errorCode.getHttpStatus())
				.body(errorCode.getMessage());
//				.code(errorCode.getCode())
//				.message(errorCode.getMessage())
//				.build();

		response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
		response.flushBuffer();
	}
}
