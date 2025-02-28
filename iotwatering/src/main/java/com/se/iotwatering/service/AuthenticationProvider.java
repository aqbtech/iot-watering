package com.se.iotwatering.service;

import com.nimbusds.jose.JOSEException;
import com.se.iotwatering.dto.http.request.AuthenticationRequest;
import com.se.iotwatering.dto.http.request.IntrospectRequest;
import com.se.iotwatering.dto.http.request.LogoutRequest;
import com.se.iotwatering.dto.http.request.RefreshRequest;
import com.se.iotwatering.dto.http.response.AuthenticationResponse;
import com.se.iotwatering.dto.http.response.IntrospectResponse;


import java.text.ParseException;

public interface AuthenticationProvider {
	IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException;
	AuthenticationResponse authenticate(AuthenticationRequest request);
	void logout(LogoutRequest request) throws ParseException, JOSEException;
	AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException;
}
