package com.se.iotwatering.dto.http.response;


import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MinimalUserProfile {
	private String username;
	private String role;
}
