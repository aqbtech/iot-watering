package com.se.iotwatering.service;

import com.se.iotwatering.dto.http.request.UserProfileUpdateRequest;
import com.se.iotwatering.dto.http.response.UserProfileResponse;
import com.se.iotwatering.entity.User;
import com.se.iotwatering.exception.ErrorCode;
import com.se.iotwatering.exception.WebServerException;
import com.se.iotwatering.repo.UserRepository;
import com.se.iotwatering.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserProfileUpdateRequest updateRequest;
    private final String TEST_USERNAME = "testuser";
    private final String TEST_FIRST_NAME = "John";
    private final String TEST_LAST_NAME = "Doe";
    private final String TEST_EMAIL = "john.doe@example.com";
    private final String TEST_PHONE = "1234567890";
    private final LocalDate TEST_DOB = LocalDate.of(1990, 1, 1);
    
    @BeforeEach
    void setUp() {
        // Create a test user
        testUser = User.builder()
                .userId(1L)
                .username(TEST_USERNAME)
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .email(TEST_EMAIL)
                .phone(TEST_PHONE)
                .dob(TEST_DOB)
                .build();
        
        // Create an update request
        updateRequest = UserProfileUpdateRequest.builder()
                .firstName("Updated First")
                .lastName("Updated Last")
                .email("updated@example.com")
                .phone("9876543210")
                .dateOfBirth("2000-01-01")
                .build();
    }
    
    @Test
    void getUserProfile_WithExistingUser_ReturnsUserProfile() {
        // Arrange
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        
        // Act
        UserProfileResponse response = userService.getUserProfile(TEST_USERNAME);
        
        // Assert
        assertNotNull(response);
        assertEquals(TEST_FIRST_NAME, response.getFirstName());
        assertEquals(TEST_LAST_NAME, response.getLastName());
        assertEquals(TEST_EMAIL, response.getEmail());
        assertEquals(TEST_PHONE, response.getPhone());
        assertEquals(TEST_DOB.toString(), response.getDateOfBirth());
        assertEquals(TEST_USERNAME, response.getUsername());
        
        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
    }
    
    @Test
    void getUserProfile_WithNonExistingUser_ThrowsException() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        
        // Act & Assert
        WebServerException exception = assertThrows(WebServerException.class, () -> 
            userService.getUserProfile("nonexistent")
        );
        
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }
    
    @Test
    void updateProfile_WithValidData_UpdatesProfile() {
        // Arrange
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // Set up security context with test user for the test
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(TEST_USERNAME, null, null));
        
        // Act
        boolean result = userService.updateProfile(updateRequest);
        
        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
        verify(userRepository, times(1)).save(any(User.class));
        
        // Verify user data was updated
        assertEquals("Updated First", testUser.getFirstName());
        assertEquals("Updated Last", testUser.getLastName());
        assertEquals("updated@example.com", testUser.getEmail());
        assertEquals("9876543210", testUser.getPhone());
        assertEquals(LocalDate.of(2000, 1, 1), testUser.getDob());
        
        // Clean up security context
        SecurityContextHolder.clearContext();
    }
    
    @Test
    void updateProfile_WithInvalidDateFormat_ThrowsException() {
        // Arrange
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        
        // Create request with invalid date format
        UserProfileUpdateRequest invalidRequest = UserProfileUpdateRequest.builder()
                .firstName("Updated First")
                .lastName("Updated Last")
                .email("updated@example.com")
                .phone("9876543210")
                .dateOfBirth("invalid-date")
                .build();
        
        // Set up security context with test user for the test
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(TEST_USERNAME, null, null));
        
        // Act & Assert
        WebServerException exception = assertThrows(WebServerException.class, () -> 
            userService.updateProfile(invalidRequest)
        );
        
        // Clean up security context
        SecurityContextHolder.clearContext();
        
        assertEquals(ErrorCode.UNKNOWN_ERROR, exception.getErrorCode());
        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void updateProfile_WithNonExistingUser_ThrowsException() {
        // Set up security context with test user for the test
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(TEST_USERNAME, null, null));
        
        // Arrange
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());
        
        // Act & Assert
        WebServerException exception = assertThrows(WebServerException.class, () -> 
            userService.updateProfile(updateRequest)
        );
        
        // Clean up security context
        SecurityContextHolder.clearContext();
        
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
        verify(userRepository, never()).save(any(User.class));
    }
}
