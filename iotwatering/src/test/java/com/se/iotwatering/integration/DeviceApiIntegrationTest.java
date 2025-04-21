package com.se.iotwatering.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.iotwatering.constant.ConfigurationDefault;
import com.se.iotwatering.dto.http.request.DeviceAddRequest;
import com.se.iotwatering.dto.http.request.DeviceConfigRequest;
import com.se.iotwatering.dto.http.request.DeviceStateRequest;
import com.se.iotwatering.entity.Sensor;
import com.se.iotwatering.entity.User;
import com.se.iotwatering.repo.SensorRepository;
import com.se.iotwatering.repo.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test") // Use test configuration
public class DeviceApiIntegrationTest {

	@Container
	@SuppressWarnings("resource")
	private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
			.withDatabaseName("testdb")
			.withUsername("test")
			.withPassword("test");
	private final String TEST_DEVICE_ID = "2b1ab270-f29a-11ef-87b5-21bccf7d29d5";
	private final String TEST_DEVICE_NAME = "Test Device";
	private final String TEST_DEVICE_LOCATION = "Test Location";
	private final String TEST_DEVICE_STATUS = "inactive";
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private SensorRepository sensorRepository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private UserRepository userRepository;

	@DynamicPropertySource
	static void overrideProps(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
		registry.add("spring.datasource.username", mysqlContainer::getUsername);
		registry.add("spring.datasource.password", mysqlContainer::getPassword);
	}

	@BeforeEach
	void setUp() {
		// Clean the database before each test
		sensorRepository.deleteAll();
		// add a test user
		User testUser = User.builder()
				.userId(1234L)
				.username("testuser")
				.password("password")
				.firstName("Test")
				.lastName("User")
				.build();
		userRepository.save(testUser);
	}
	@AfterEach
	void cleanUp() {
		// Clean the database after each test
		sensorRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	@WithMockUser(username = "testuser")
	void testAddDevice() throws Exception {
		// Create device request
		DeviceAddRequest request = DeviceAddRequest.builder()
				.coreIotDeviceId(TEST_DEVICE_ID)
				.deviceName(TEST_DEVICE_NAME)
				.deviceLocation(TEST_DEVICE_LOCATION)
				.deviceStatus(TEST_DEVICE_STATUS)
				.build();

		// Send request to API
		mockMvc.perform(post("/device/add")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.result", is("Thiết bị đã được thêm")));

		// Verify device was added to the database
		Optional<Sensor> sensor = sensorRepository.findByPureSensorId(TEST_DEVICE_ID);
		assertTrue(sensor.isPresent());
		assertEquals(TEST_DEVICE_ID, sensor.get().getPureSensorId());
		assertEquals(TEST_DEVICE_NAME, sensor.get().getName());
		assertEquals(TEST_DEVICE_LOCATION, sensor.get().getLocation());
		assertEquals(TEST_DEVICE_STATUS, sensor.get().getStatus());
	}

	@Test
	@WithMockUser(username = "testuser")
	void testSetDeviceConfiguration() throws Exception {
		// First, add a device
		addTestDevice();

		// Create configuration request
		DeviceConfigRequest configRequest = DeviceConfigRequest.builder()
				.deviceId(TEST_DEVICE_ID)
				.temperature(ConfigurationDefault.DEFAULT_TEMPERATURE)
				.humidity(ConfigurationDefault.DEFAULT_HUMIDITY)
				.soilMoisture(ConfigurationDefault.DEFAULT_SOIL_MOISTURE)
				.light(ConfigurationDefault.DEFAULT_LIGHT)
				.build();

		// Send configuration request
		mockMvc.perform(post("/device/setConfig")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(configRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.result", is("Đã cập nhật cấu hình")));

		// Verify configuration was updated
		Optional<Sensor> sensor = sensorRepository.findByPureSensorId(TEST_DEVICE_ID);
		assertTrue(sensor.isPresent());
		assertEquals(ConfigurationDefault.DEFAULT_HUMIDITY, sensor.get().getConfiguration().getHumidity());
		assertEquals(ConfigurationDefault.DEFAULT_SOIL_MOISTURE, sensor.get().getConfiguration().getSoilMoisture());
		assertEquals(ConfigurationDefault.DEFAULT_LIGHT, sensor.get().getConfiguration().getLight());
		assertEquals(ConfigurationDefault.DEFAULT_TEMPERATURE, sensor.get().getConfiguration().getTemperature());
	}

	@Test
	@WithMockUser(username = "testuser")
	void testControlLight() throws Exception {
		// First, add a device
		addTestDevice();

		// Create a light control request (turn on)
		DeviceStateRequest stateRequest = DeviceStateRequest.builder()
				.deviceId(TEST_DEVICE_ID)
				.state(true)
				.build();

		// Send light control request
		mockMvc.perform(post("/device/light")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(stateRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.result", is("Đã cập nhật trạng thái đèn")));

		// Verify light status was updated in the database
	}

	@Test
	@WithMockUser(username = "testuser")
	void testControlPump() throws Exception {
		// First, add a device
		addTestDevice();

		// Create a pump control request (turn on)
		DeviceStateRequest stateRequest = DeviceStateRequest.builder()
				.deviceId(TEST_DEVICE_ID)
				.state(true)
				.build();

		// Send pump control request
		mockMvc.perform(post("/device/pump")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(stateRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.result", is("Đã cập nhật trạng thái máy bơm")));

		// Verify pump status was updated in the database
	}

	@Test
	@WithMockUser(username = "testuser")
	void testGetDeviceInfo() throws Exception {
		// First, add a device and update its configuration
		addTestDevice();
		updateTestDeviceConfiguration();

		// Get device info
		mockMvc.perform(get("/device/info")
						.param("deviceId", TEST_DEVICE_ID))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.result.name", is(TEST_DEVICE_NAME)))
				.andExpect(jsonPath("$.result.location", is(TEST_DEVICE_LOCATION)))
				.andExpect(jsonPath("$.result.light", is("1")))
				.andExpect(jsonPath("$.result.pump", is("1")))
				.andExpect(jsonPath("$.result.fan", is("1")))
				.andExpect(jsonPath("$.result.siren", is("1")))
				.andExpect(jsonPath("$.result.configLight", is(ConfigurationDefault.DEFAULT_LIGHT)))
				.andExpect(jsonPath("$.result.configPump", is(ConfigurationDefault.DEFAULT_SOIL_MOISTURE)))
				.andExpect(jsonPath("$.result.configFan", is(ConfigurationDefault.DEFAULT_HUMIDITY)))
				.andExpect(jsonPath("$.result.configSiren", is(ConfigurationDefault.DEFAULT_TEMPERATURE)));
	}

	// Helper method to add a test device for other tests
	private void addTestDevice() throws Exception {
		DeviceAddRequest request = DeviceAddRequest.builder()
				.coreIotDeviceId(TEST_DEVICE_ID)
				.deviceName(TEST_DEVICE_NAME)
				.deviceLocation(TEST_DEVICE_LOCATION)
				.deviceStatus(TEST_DEVICE_STATUS)
				.build();

		mockMvc.perform(post("/device/add")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk());
	}

	// Helper method to update a test device configuration
	private void updateTestDeviceConfiguration() throws Exception {
		DeviceConfigRequest configRequest = DeviceConfigRequest.builder()
				.deviceId(TEST_DEVICE_ID)
				.temperature(ConfigurationDefault.DEFAULT_TEMPERATURE)
				.humidity(ConfigurationDefault.DEFAULT_HUMIDITY)
				.soilMoisture(ConfigurationDefault.DEFAULT_SOIL_MOISTURE)
				.light(ConfigurationDefault.DEFAULT_LIGHT)
				.build();

		mockMvc.perform(post("/device/setConfig")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(configRequest)))
				.andExpect(status().isOk());
	}

	// Failure Test Cases

    @Test
	@WithMockUser(username = "testuser")
    void testAddDuplicateDevice() throws Exception {
        // First, add a device
        addTestDevice();

        // Try to add the same device again
        DeviceAddRequest request = DeviceAddRequest.builder()
                .coreIotDeviceId(TEST_DEVICE_ID)
                .deviceName(TEST_DEVICE_NAME)
                .deviceLocation(TEST_DEVICE_LOCATION)
                .deviceStatus(TEST_DEVICE_STATUS)
                .build();

        // Adding the same device should return false in response
        mockMvc.perform(post("/device/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is("Không thể thêm thiết bị")));
    }

	@Test
	@WithMockUser(username = "testuser")
	void testSetConfigForNonExistentDevice() throws Exception {
		// Create configuration request for non-existent device
		String nonExistentDeviceId = "device-does-not-exist";
		DeviceConfigRequest configRequest = DeviceConfigRequest.builder()
				.deviceId(nonExistentDeviceId)
				.temperature(25)
				.humidity(80)
				.soilMoisture(60)
				.light(70)
				.build();

		// Should throw exception that gets mapped to an error response
		mockMvc.perform(post("/device/setConfig")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(configRequest)))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "testuser")
	void testControlLightForNonExistentDevice() throws Exception {
		// Create a light control request for a non-existent device
		String nonExistentDeviceId = "device-does-not-exist";
		DeviceStateRequest stateRequest = DeviceStateRequest.builder()
				.deviceId(nonExistentDeviceId)
				.state(true)
				.build();

		// Should throw an exception that gets mapped to an error response
		mockMvc.perform(post("/device/light")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(stateRequest)))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "testuser")
	void testGetInfoForNonExistentDevice() throws Exception {
		// Get info for a non-existent device
		String nonExistentDeviceId = "device-does-not-exist";

		// Should throw an exception that gets mapped to the error response
		mockMvc.perform(get("/device/info")
						.param("deviceId", nonExistentDeviceId))
				.andExpect(status().isNotFound());
	}


	@Test
	@WithMockUser(username = "testuser")
	void testMissingRequiredParameter() throws Exception {
		// Create device request with missing required field
		String incompleteJson = "{}";

		// Should return a bad request
		mockMvc.perform(post("/device/add")
						.contentType(MediaType.APPLICATION_JSON)
						.content(incompleteJson))
				.andExpect(status().isBadRequest());
	}
}
