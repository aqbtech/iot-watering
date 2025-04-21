package com.se.iotwatering.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.iotwatering.dto.http.request.AuthenticationRequest;
import com.se.iotwatering.dto.http.request.UserProfileUpdateRequest;
import com.se.iotwatering.entity.User;
import com.se.iotwatering.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test") // Use test configuration
public class UserApiIntegrationTest {

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

    // Test user credentials
    private final String TEST_USERNAME = "testuser";
    private final String TEST_PASSWORD = "testpassword";
    private final String TEST_FIRST_NAME = "Test";
    private final String TEST_LAST_NAME = "User";
    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_PHONE = "0123456789";
    private final String TEST_DOB = "1990-01-01";

    // We'll use the loginAndGetToken method instead of storing the token as a field

    @BeforeEach
    void setUp() {
        // Clean the database before each test
        userRepository.deleteAll();
        
        // Create a test user for authentication tests
        createTestUser();
    }

    @Test
    void testUserLogin() throws Exception {
        // Create authentication request
        AuthenticationRequest authRequest = AuthenticationRequest.builder()
                .username(TEST_USERNAME)
                .password(TEST_PASSWORD)
                .build();

        // Send a login request and verify success
        mockMvc.perform(post("/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.token", notNullValue()));
    }

    @Test
    void testUserLoginWithInvalidCredentials() throws Exception {
        // Create authentication request with invalid password
        AuthenticationRequest authRequest = AuthenticationRequest.builder()
                .username(TEST_USERNAME)
                .password("wrongpassword")
                .build();

        // Send login request and verify failure
        mockMvc.perform(post("/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateUserProfile() throws Exception {
        // First, login to get an authentication token
        String token = loginAndGetToken();

        // Create profile update request
        UserProfileUpdateRequest updateRequest = UserProfileUpdateRequest.builder()
                .firstName("Updated")
                .lastName("Name")
                .email("updated@example.com")
                .phone("9876543210")
                .dateOfBirth("1995-05-05")
                .build();

        // Send an update request with authentication
        mockMvc.perform(put("/user/v1/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is("Cập nhật thành công")));

        // Verify profile was updated in the database
        Optional<User> updatedUser = userRepository.findByUsername(TEST_USERNAME);
        assertTrue(updatedUser.isPresent());
        assertEquals("Updated", updatedUser.get().getFirstName());
        assertEquals("Name", updatedUser.get().getLastName());
        assertEquals("updated@example.com", updatedUser.get().getEmail());
        assertEquals("9876543210", updatedUser.get().getPhone());
        assertEquals(LocalDate.parse("1995-05-05"), updatedUser.get().getDob());
    }

    @Test
    void testUpdateUserProfileWithoutAuthentication() throws Exception {
        // Create profile update request
        UserProfileUpdateRequest updateRequest = UserProfileUpdateRequest.builder()
                .firstName("Updated")
                .lastName("Name")
                .email("updated@example.com")
                .phone("9876543210")
                .dateOfBirth("1995-05-05")
                .build();

        // Send an update request without authentication
        mockMvc.perform(put("/user/v1/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUserProfile() throws Exception {
        // First, login to get an authentication token
        String token = loginAndGetToken();

        // Get a user profile with authentication
        mockMvc.perform(get("/user/v1/profile/" + TEST_USERNAME)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.firstName", is(TEST_FIRST_NAME)))
                .andExpect(jsonPath("$.result.lastName", is(TEST_LAST_NAME)))
                .andExpect(jsonPath("$.result.email", is(TEST_EMAIL)))
                .andExpect(jsonPath("$.result.phone", is(TEST_PHONE)))
                .andExpect(jsonPath("$.result.dateOfBirth", is(TEST_DOB)));
    }

    @Test
    void testGetUserProfileWithoutAuthentication() throws Exception {
        // Get a user profile without authentication
        mockMvc.perform(get("/user/v1/profile/" + TEST_USERNAME))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetNonExistentUserProfile() throws Exception {
        // First, login to get an authentication token
        String token = loginAndGetToken();

        // Get profile for non-existent user
        mockMvc.perform(get("/user/v1/profile/nonexistentuser")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    // Helper methods

    private void createTestUser() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        User user = User.builder()
            .username(TEST_USERNAME)
            .password(passwordEncoder.encode(TEST_PASSWORD)) // In a real scenario, this should be encoded
            .firstName(TEST_FIRST_NAME)
            .lastName(TEST_LAST_NAME)
            .email(TEST_EMAIL)
            .phone(TEST_PHONE)
            .dob(LocalDate.parse(TEST_DOB))
            .build();
        userRepository.save(user);
    }

    private String loginAndGetToken() throws Exception {
        AuthenticationRequest authRequest = AuthenticationRequest.builder()
                .username(TEST_USERNAME)
                .password(TEST_PASSWORD)
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
}
