package com.se.iotwatering.dto.http.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
// best practice this should implement contract interface
public class AuthenticationResponse {
	private String token;
	private boolean authenticatedToken;
	private String role;
}
