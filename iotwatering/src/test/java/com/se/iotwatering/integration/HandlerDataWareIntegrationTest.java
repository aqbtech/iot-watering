package com.se.iotwatering.integration;

import com.se.iotwatering.entity.Configuration;
import com.se.iotwatering.entity.Sensor;
import com.se.iotwatering.entity.SensorData;
import com.se.iotwatering.repo.SensorRepository;
import com.se.iotwatering.repository.AlertStateRepository;
import com.se.iotwatering.service.impl.HandlerDataWare;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test") // Use test configuration
class HandlerDataWareIntegrationTest {
	@Container
	@SuppressWarnings("resource")
	private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
			.withDatabaseName("testdb")
			.withUsername("test")
			.withPassword("test");
	@Autowired
	private SensorRepository sensorRepository;
	@Autowired
	private HandlerDataWare handlerDataWare;
	@Autowired
	private AlertStateRepository alertStateRepository;
	private SensorData mockData;

	@DynamicPropertySource
	static void overrideProps(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
		registry.add("spring.datasource.username", mysqlContainer::getUsername);
		registry.add("spring.datasource.password", mysqlContainer::getPassword);
	}

	@BeforeEach
	void setup() {
		Sensor mockSensor = Sensor.builder().pureSensorId("2b1ab270-f29a-11ef-87b5-21bccf7d29d5").build();
		Configuration mockConfig = Configuration.builder()
				.temperature(30.0)
				.light(200.0)
				.soilMoisture(40.0)
				.autoControlEnabled(true)
				.build();
		mockSensor.setConfiguration(mockConfig);
		mockData = new SensorData();
		mockData.setSensor(mockSensor);
		mockData.setTemperature("35.0"); // vượt ngưỡng nhiệt độ
		mockData.setLight("250.0"); // đủ ánh sáng
		mockData.setSoilMoisture("50.0"); // đủ ẩm
		// save sensor to a database
		sensorRepository.save(mockSensor);
	}

	@Test
	void testThresholdCheck_sendEmailAndTurnOnFanWhenOverTemp() {
		// Khi nhiệt độ vượt ngưỡng, sau thresholdCheck, alertState phải được lưu và trạng thái alerted=true
		handlerDataWare.thresholdCheck(mockData);
		var alertOpt = alertStateRepository.findBySensorIdAndField("2b1ab270-f29a-11ef-87b5-21bccf7d29d5", "temperature");
		assertThat(alertOpt).isPresent();
		assertThat(alertOpt.get().isAlerted()).isTrue();
		// Có thể kiểm tra thêm log hoặc các trạng thái khác nếu cần
	}

	@Test
	void testThresholdCheck_noActionWhenBelowThreshold() {
		mockData.setTemperature("25.0"); // dưới ngưỡng nhiệt độ
		handlerDataWare.thresholdCheck(mockData);
		var alertOpt = alertStateRepository.findBySensorIdAndField("2b1ab270-f29a-11ef-87b5-21bccf7d29d5", "temperature");
		// Nếu chưa từng vượt ngưỡng thì không có alert
		assertThat(alertOpt).isNotPresent();
	}
}
