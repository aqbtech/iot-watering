package com.se.iotwatering.service;



import com.se.iotwatering.dto.http.request.UserRegister;
import com.se.iotwatering.dto.http.response.MinimalUserProfile;

public interface GuestService {
	MinimalUserProfile register(UserRegister userRegister);
}
