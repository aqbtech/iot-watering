package com.se.iotwatering.util;

import com.se.iotwatering.exception.AuthErrorCode;
import com.se.iotwatering.exception.WebServerException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

public class SecurityUtil {

	public static String getCurrentUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new WebServerException(AuthErrorCode.UNAUTHENTICATED);
//			return null;
		}

		Object principal = authentication.getPrincipal();
		if (principal instanceof UserDetails) {
			return ((UserDetails) principal).getUsername();
		} else if (principal instanceof String) {
			return (String) principal;
		} else if (principal instanceof Jwt) {
			return ((Jwt) principal).getSubject();
		}

		throw new WebServerException(AuthErrorCode.UNAUTHENTICATED);
	}

	// Hoặc getCurrentUserId() nếu có userId
}

