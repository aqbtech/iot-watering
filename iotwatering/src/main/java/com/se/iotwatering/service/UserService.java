package com.se.iotwatering.service;

import com.se.iotwatering.dto.http.request.UserProfileUpdateRequest;
import com.se.iotwatering.dto.http.response.UserProfileResponse;

public interface UserService {
    /**
     * Update the profile information of a user
     * @param request Profile update request with new user information
     * @return True if the update was successful
     */
    boolean updateProfile(UserProfileUpdateRequest request);
    
    /**
     * Get user profile information by username
     * @param username Username of the user
     * @return User profile information
     */
    UserProfileResponse getUserProfile(String username);
}
