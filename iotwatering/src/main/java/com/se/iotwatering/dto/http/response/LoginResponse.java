package com.se.iotwatering.dto.http.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String username;
    private String dateOfBirth;
    private String token;
}
