package com.se.iotwatering.service.impl;

import com.se.iotwatering.dto.http.request.UserProfileUpdateRequest;
import com.se.iotwatering.dto.http.response.UserProfileResponse;
import com.se.iotwatering.entity.User;
import com.se.iotwatering.exception.AuthErrorCode;
import com.se.iotwatering.exception.BaseErrorCode;
import com.se.iotwatering.exception.UserErrorCode;
import com.se.iotwatering.exception.WebServerException;
import com.se.iotwatering.repo.UserRepository;
import com.se.iotwatering.service.UserService;
import com.se.iotwatering.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public boolean updateProfile(UserProfileUpdateRequest request) {
        String username = SecurityUtil.getCurrentUsername();
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new WebServerException(UserErrorCode.USER_NOT_FOUND));
        
        // Update user information
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        
        // Parse and set date of birth
        try {
            LocalDate dob = LocalDate.parse(request.getDateOfBirth());
            user.setDob(dob);
        } catch (Exception e) {
            log.error("Failed to parse date of birth: {}", request.getDateOfBirth(), e);
            throw new WebServerException(BaseErrorCode.UNKNOWN_ERROR);
        }
        
        // Save an updated user
        userRepository.save(user);
        
        return true;
    }

    @Override
    public UserProfileResponse getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new WebServerException(UserErrorCode.USER_NOT_FOUND));
        
        return UserProfileResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .dateOfBirth(user.getDob() != null ? user.getDob().toString() : null)
                .username(user.getUsername())
                .build();
    }
    
    /**
     * Get the username of the currently authenticated user
     * In a real implementation, this would use Spring Security's authentication context
     * @return The username of the currently authenticated user
     */
    private String getCurrentUsername() {
        // In a real implementation, you would get this from the security context
        // SecurityContextHolder.getContext().getAuthentication().getName();
        
        // For demonstration purposes, we'll throw an exception
        // This should be replaced with proper security integration
        throw new WebServerException(AuthErrorCode.UNAUTHORIZED);
    }
}
