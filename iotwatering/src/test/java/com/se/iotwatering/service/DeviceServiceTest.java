package com.se.iotwatering.service;

import com.se.iotwatering.constant.ConfigurationDefault;
import com.se.iotwatering.dto.http.request.DeviceAddRequest;
import com.se.iotwatering.dto.http.request.DeviceConfigRequest;
import com.se.iotwatering.dto.http.request.DeviceStateRequest;
import com.se.iotwatering.dto.http.response.DeviceInfoResponse;
import com.se.iotwatering.entity.Configuration;
import com.se.iotwatering.entity.Sensor;
import com.se.iotwatering.entity.User;
import com.se.iotwatering.exception.ErrorCode;
import com.se.iotwatering.exception.WebServerException;
import com.se.iotwatering.mapper.Device2Sensor;
import com.se.iotwatering.repo.SensorRepository;
import com.se.iotwatering.repo.UserRepository;
import com.se.iotwatering.service.impl.DeviceServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private SensorRepository sensorRepository;
    @Mock
    private Device2Sensor device2Sensor;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeviceServiceImpl deviceService;

    private Sensor testSensor;
    private Configuration testConfig;
    private User testUser;
    private final String TEST_DEVICE_ID = "test-device-123";
    private final String TEST_DEVICE_NAME = "Test Device";
    private final String TEST_LOCATION = "Test Location";

    @BeforeEach
    void setUp() {
        // Create a test configuration
        testConfig = Configuration.builder()
                .humidity(ConfigurationDefault.DEFAULT_HUMIDITY)
                .soilMoisture(ConfigurationDefault.DEFAULT_SOIL_MOISTURE)
                .light(ConfigurationDefault.DEFAULT_LIGHT)
                .temperature(ConfigurationDefault.DEFAULT_TEMPERATURE)
                .build();

        // Create a test sensor using the builder
        testSensor = Sensor.builder()
                .pureSensorId(TEST_DEVICE_ID)
                .name(TEST_DEVICE_NAME)
                .location(TEST_LOCATION)
                .status("inactive")
                .configuration(testConfig)
                .build();
        // Create a test user
        testUser = User.builder()
                .username("testuser")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .userId(123L)
                .dob(null)
                .build();
    }

    @BeforeEach
    void setupAuthentication() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testuser", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }


    @Test
    @WithMockUser(username = "testuser", password = "password")
    void addDevice_WithNewDevice_ReturnsTrue() {
        // Arrange
        DeviceAddRequest request = DeviceAddRequest.builder()
                .coreIotDeviceId(TEST_DEVICE_ID)
                .deviceName(TEST_DEVICE_NAME)
                .deviceLocation(TEST_LOCATION)
                .deviceStatus("inactive")
                .build();
        
        when(device2Sensor.toSensor(request)).thenReturn(testSensor);
        when(sensorRepository.existsByPureSensorId(TEST_DEVICE_ID)).thenReturn(false);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(testUser));
        when(sensorRepository.save(any(Sensor.class))).thenReturn(testSensor);
        // Act
        boolean result = deviceService.addDevice(request);
        
        // Assert
        assertTrue(result);
        verify(sensorRepository).existsByPureSensorId(TEST_DEVICE_ID);
        verify(sensorRepository).save(any(Sensor.class));
        
        // Capture the Sensor that was saved to verify its properties
        ArgumentCaptor<Sensor> sensorCaptor = ArgumentCaptor.forClass(Sensor.class);
        verify(sensorRepository).save(sensorCaptor.capture());
        Sensor savedSensor = sensorCaptor.getValue();
        
        assertEquals(TEST_DEVICE_ID, savedSensor.getPureSensorId());
        assertEquals(TEST_DEVICE_NAME, savedSensor.getName());
        assertEquals(TEST_LOCATION, savedSensor.getLocation());
        assertEquals("inactive", savedSensor.getStatus());
        // assert configuration
        assertEquals(testConfig.getHumidity(), savedSensor.getConfiguration().getHumidity());
        assertEquals(testConfig.getSoilMoisture(), savedSensor.getConfiguration().getSoilMoisture());
        assertEquals(testConfig.getLight(), savedSensor.getConfiguration().getLight());
        assertEquals(testConfig.getTemperature(), savedSensor.getConfiguration().getTemperature());
        // assert user
        assertEquals(testUser.getUsername(), savedSensor.getUsers().getFirst().getUsername());
    }
    
    @Test
    void addDevice_WithExistingDevice_ReturnsFalse() {
        // Arrange
        DeviceAddRequest request = DeviceAddRequest.builder()
                .coreIotDeviceId(TEST_DEVICE_ID)
                .deviceName(TEST_DEVICE_NAME)
                .deviceLocation(TEST_LOCATION)
                .deviceStatus("inactive")
                .build();
        
        when(sensorRepository.existsByPureSensorId(TEST_DEVICE_ID)).thenReturn(true);
        
        // Act
        boolean result = deviceService.addDevice(request);
        
        // Assert
        assertFalse(result);
        verify(sensorRepository).existsByPureSensorId(TEST_DEVICE_ID);
        verify(sensorRepository, never()).save(any(Sensor.class));
    }
    
    @Test
    void setConfig_WithExistingDevice_UpdatesConfigAndReturnsTrue() {
        // Arrange
        DeviceConfigRequest request = DeviceConfigRequest.builder()
                .deviceId(TEST_DEVICE_ID)
                .humidity(85.0)
                .soilMoisture(65.0)
                .light(75.0)
                .temperature(30.0)
                .build();
        
        when(sensorRepository.findByPureSensorId(TEST_DEVICE_ID)).thenReturn(Optional.of(testSensor));
        when(sensorRepository.save(any(Sensor.class))).thenReturn(testSensor);
        
        // Act
        boolean result = deviceService.setConfig(request);
        
        // Assert
        assertTrue(result);
        verify(sensorRepository).findByPureSensorId(TEST_DEVICE_ID);
        verify(sensorRepository).save(any(Sensor.class));
        
        // Capture the Configuration that was set
        ArgumentCaptor<Configuration> configCaptor = ArgumentCaptor.forClass(Configuration.class);
        verify(testSensor).setConfiguration(configCaptor.capture());
        Configuration updatedConfig = configCaptor.getValue();
        
        assertEquals("85.0", updatedConfig.getHumidity());
        assertEquals("65.0", updatedConfig.getSoilMoisture());
        assertEquals("75.0", updatedConfig.getLight());
        assertEquals(30.0, updatedConfig.getTemperature());
    }
    
    @Test
    void setConfig_WithNonExistingDevice_ThrowsException() {
        // Arrange
        DeviceConfigRequest request = DeviceConfigRequest.builder()
                .deviceId("non-existent-device")
                .humidity(85.0)
                .soilMoisture(65.0)
                .light(75.0)
                .temperature(30.0)
                .build();
        
        when(sensorRepository.findByPureSensorId("non-existent-device")).thenReturn(Optional.empty());
        
        // Act & Assert
        WebServerException exception = assertThrows(WebServerException.class, () ->
            deviceService.setConfig(request)
        );
        
        assertEquals(ErrorCode.DEVICE_NOT_FOUND, exception.getErrorCode());
        verify(sensorRepository).findByPureSensorId("non-existent-device");
        verify(sensorRepository, never()).save(any(Sensor.class));
    }
    
    @Test
    void controlLight_WithExistingDevice_UpdatesStateAndReturnsTrue() {
        // Arrange
        DeviceStateRequest request = DeviceStateRequest.builder()
                .deviceId(TEST_DEVICE_ID)
                .state(true)
                .build();
        
        when(sensorRepository.findByPureSensorId(TEST_DEVICE_ID)).thenReturn(Optional.of(testSensor));
        when(sensorRepository.save(any(Sensor.class))).thenReturn(testSensor);
        
        // Act
        boolean result = deviceService.controlLight(request);
        
        // Assert
        assertTrue(result);
        verify(testSensor).setStatus("active");
        verify(sensorRepository).save(testSensor);
    }
    
    @Test
    void controlPump_WithExistingDevice_UpdatesStateAndReturnsTrue() {
        // Arrange
        DeviceStateRequest request = DeviceStateRequest.builder()
                .deviceId(TEST_DEVICE_ID)
                .state(true)
                .build();
        
        when(sensorRepository.findByPureSensorId(TEST_DEVICE_ID)).thenReturn(Optional.of(testSensor));
        when(sensorRepository.save(any(Sensor.class))).thenReturn(testSensor);
        
        // Act
        boolean result = deviceService.controlPump(request);
        
        // Assert
        assertTrue(result);
        verify(testSensor).setStatus("active");
        verify(sensorRepository).save(testSensor);
    }
    
    @Test
    void controlSiren_WithExistingDevice_UpdatesStateAndReturnsTrue() {
        // Arrange
        DeviceStateRequest request = DeviceStateRequest.builder()
                .deviceId(TEST_DEVICE_ID)
                .state(true)
                .build();
        
        when(sensorRepository.findByPureSensorId(TEST_DEVICE_ID)).thenReturn(Optional.of(testSensor));
        when(sensorRepository.save(any(Sensor.class))).thenReturn(testSensor);
        
        // Act
        boolean result = deviceService.controlSiren(request);
        
        // Assert
        assertTrue(result);
        verify(testSensor).setStatus("active");
        verify(sensorRepository).save(testSensor);
    }
    
    @Test
    void controlFan_WithExistingDevice_UpdatesStateAndReturnsTrue() {
        // Arrange
        DeviceStateRequest request = DeviceStateRequest.builder()
                .deviceId(TEST_DEVICE_ID)
                .state(true)
                .build();
        
        when(sensorRepository.findByPureSensorId(TEST_DEVICE_ID)).thenReturn(Optional.of(testSensor));
        when(sensorRepository.save(any(Sensor.class))).thenReturn(testSensor);
        
        // Act
        boolean result = deviceService.controlFan(request);
        
        // Assert
        assertTrue(result);
        verify(testSensor).setStatus("active");
        verify(sensorRepository).save(testSensor);
    }
    
    @Test
    void getDeviceInfo_WithExistingDevice_ReturnsDeviceInfo() {
        // Arrange
        when(sensorRepository.findByPureSensorId(TEST_DEVICE_ID)).thenReturn(Optional.of(testSensor));
        
        // Act
        DeviceInfoResponse response = deviceService.getDeviceInfo(TEST_DEVICE_ID);
        
        // Assert
        assertNotNull(response);
        assertEquals(TEST_DEVICE_NAME, response.getName());
        assertEquals(TEST_LOCATION, response.getLocation());
        assertEquals("inactive", response.getLight());
        assertEquals("inactive", response.getPump());
        assertEquals("inactive", response.getSiren());
        assertEquals("inactive", response.getFan());
        assertEquals(70.0, response.getConfigLight());
        assertEquals(60.0, response.getConfigPump());
        assertEquals(80.0, response.getConfigFan());
        assertEquals(25.0, response.getConfigSiren());
        
        verify(sensorRepository).findByPureSensorId(TEST_DEVICE_ID);
    }
    
    @Test
    void getDeviceInfo_WithNonExistingDevice_ThrowsException() {
        // Arrange
        when(sensorRepository.findByPureSensorId("non-existent-device")).thenReturn(Optional.empty());
        
        // Act & Assert
        WebServerException exception = assertThrows(WebServerException.class, () -> 
            deviceService.getDeviceInfo("non-existent-device")
        );
        
        assertEquals(ErrorCode.DEVICE_NOT_FOUND, exception.getErrorCode());
        verify(sensorRepository).findByPureSensorId("non-existent-device");
    }
    
    @Test
    void controlLight_WithNonExistingDevice_ThrowsException() {
        // Arrange
        DeviceStateRequest request = DeviceStateRequest.builder()
                .deviceId("non-existent-device")
                .state(true)
                .build();
                
        when(sensorRepository.findByPureSensorId("non-existent-device")).thenReturn(Optional.empty());
        
        // Act & Assert
        WebServerException exception = assertThrows(WebServerException.class, () ->
            deviceService.controlLight(request)
        );
        
        assertEquals(ErrorCode.DEVICE_NOT_FOUND, exception.getErrorCode());
        verify(sensorRepository).findByPureSensorId("non-existent-device");
        verify(sensorRepository, never()).save(any(Sensor.class));
    }
}
