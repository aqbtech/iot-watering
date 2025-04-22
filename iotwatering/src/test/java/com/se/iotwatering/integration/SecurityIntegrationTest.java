package com.se.iotwatering.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.iotwatering.dto.http.request.AuthenticationRequest;
import com.se.iotwatering.dto.http.request.IntrospectRequest;
import com.se.iotwatering.dto.http.request.RefreshRequest;
import com.se.iotwatering.entity.User;
import com.se.iotwatering.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test") // Use test configuration
public class SecurityIntegrationTest {

    @SuppressWarnings("resource")
    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Test user credentials
    private final String TEST_ADMIN_USERNAME = "adminuser";
    private final String TEST_ADMIN_PASSWORD = "adminpassword";
    private final String TEST_USER_USERNAME = "regularuser";
    private final String TEST_USER_PASSWORD = "userpassword";

    private String adminToken;
    private String userToken;
    private String refreshToken;

    @BeforeEach
    void setUp() {
        // Clean up the database before each test
        userRepository.deleteAll();
        
        // Create test users with different roles
        createTestUser(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, "ADMIN");
        createTestUser(TEST_USER_USERNAME, TEST_USER_PASSWORD, "USER");
        
        // Get tokens for the test users
        try {
            adminToken = loginAndGetToken(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD);
            userToken = loginAndGetToken(TEST_USER_USERNAME, TEST_USER_PASSWORD);
            refreshToken = loginAndGetRefreshToken(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testAuthenticationSuccess() throws Exception {
        // Create authentication request
        AuthenticationRequest authRequest = AuthenticationRequest.builder()
                .username(TEST_ADMIN_USERNAME)
                .password(TEST_ADMIN_PASSWORD)
                .build();

        // Send authentication request and verify success
        mockMvc.perform(post("/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.token", notNullValue()))
                .andExpect(jsonPath("$.result.refreshToken", notNullValue()));
    }

    @Test
    void testAuthenticationFailure() throws Exception {
        // Create authentication request with wrong password
        AuthenticationRequest authRequest = AuthenticationRequest.builder()
                .username(TEST_ADMIN_USERNAME)
                .password("wrongpassword")
                .build();

        // Send authentication request and verify failure
        mockMvc.perform(post("/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testTokenIntrospection() throws Exception {
        // Create introspect request with a valid token
        IntrospectRequest introspectRequest = IntrospectRequest.builder()
                .token(adminToken)
                .build();

        // Send introspection request and verify success
        mockMvc.perform(post("/auth/introspect")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(introspectRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.active", is(true)))
                .andExpect(jsonPath("$.result.username", is(TEST_ADMIN_USERNAME)));
    }

    @Test
    void testTokenRefresh() throws Exception {
        // Create refresh request
        RefreshRequest refreshRequest = RefreshRequest.builder()
                .refreshToken(refreshToken)
                .build();

        // Send refresh request and verify success
        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.token", notNullValue()))
                .andExpect(jsonPath("$.result.refreshToken", notNullValue()));
    }

    @Test
    void testPublicEndpointAccess() throws Exception {
        // Try to access a public endpoint without authentication
        mockMvc.perform(get("/auth/monitor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is("OK")));
    }

    @Test
    void testProtectedEndpointWithAuthentication() throws Exception {
        // Try to access a protected endpoint with authentication
        mockMvc.perform(get("/user/v1/profile/" + TEST_ADMIN_USERNAME)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testProtectedEndpointWithoutAuthentication() throws Exception {
        // Try to access a protected endpoint without authentication
        mockMvc.perform(get("/user/v1/profile/" + TEST_ADMIN_USERNAME))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAdminProtectedEndpointWithAdminRole() throws Exception {
        // This test assumes you have an admin-only endpoint
        // You may need to adjust this to match your API
        mockMvc.perform(get("/admin/dashboard")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testAdminProtectedEndpointWithUserRole() throws Exception {
        // Try to access an admin-only endpoint with regular user role
        mockMvc.perform(get("/admin/dashboard")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeviceProtectedEndpointWithAuthentication() throws Exception {
        // Try to access a device endpoint with authentication
        mockMvc.perform(get("/device/info")
                .param("deviceId", "test-device")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());  // Expects 404 because device doesn't exist, but should be authorized
    }

    // Helper methods

    private void createTestUser(String username, String password, String role) {
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))  // Use encoded password for security
                .firstName("Test")
                .lastName("User")
                .email(username + "@example.com")
                .phone("0123456789")
                .dob(LocalDate.of(1990, 1, 1))
                .build();
        
        // Set role field if your User entity has it
        try {
            user.getClass().getMethod("setRole", String.class).invoke(user, role);
        } catch (Exception e) {
            // If no setRole method exists, try other approaches based on your security model
        }
        
        userRepository.save(user);
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        AuthenticationRequest authRequest = AuthenticationRequest.builder()
                .username(username)
                .password(password)
                .build();

        MvcResult result = mockMvc.perform(post("/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseString)
                .path("result")
                .path("token")
                .asText();
    }

    private String loginAndGetRefreshToken(String username, String password) throws Exception {
        AuthenticationRequest authRequest = AuthenticationRequest.builder()
                .username(username)
                .password(password)
                .build();

        MvcResult result = mockMvc.perform(post("/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseString)
                .path("result")
                .path("refreshToken")
                .asText();
    }
}
