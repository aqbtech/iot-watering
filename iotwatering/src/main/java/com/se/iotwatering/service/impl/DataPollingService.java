package com.se.iotwatering.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.iotwatering.dto.SensorData;
import com.se.iotwatering.dto.SensorDataDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
@Setter
public class DataPollingService {
    private final TelemetryService telemetryService;
    private final ObjectMapper objectMapper;
    private String activeDevice;
    private boolean isFetching = false;
    private Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

    public void startFetching() {
        if (!isFetching) {
            isFetching = true;
            log.info("Started fetching data for active devices.");
            fetchDataForDevice();
        }
    }

    public void stopFetching() {
        if (isFetching) {
            isFetching = false;
            log.info("Stopped fetching data for active devices.");
        }
    }

    public void resetSink() {
        this.sink.tryEmitComplete();
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
        log.info("Sink has been reset.");
    }

    @Scheduled(fixedDelay = 7000)
    public void fetchDataForDevice() {
        if (!isFetching || sink.currentSubscriberCount() == 0) {
            log.warn("No active subscribers or fetching is paused, skipping fetch...");
            return;
        }
        String deviceId = activeDevice;
        if (deviceId == null) {
            log.warn("No active devices found, skipping fetch...");
            return;
        }

        log.info("Fetching data for device: {}", deviceId);
        try {
            long id = Long.parseLong(deviceId);
            SensorData latestData = telemetryService.getLatestTelemetry(id);
            if (latestData == null) {
                log.warn("No telemetry data found for device: {}", deviceId);
                return;
            }

            SensorDataDTO sensorDataDTO = SensorDataDTO.builder()
                    .soilMoisture(latestData.getSoilMoisture())
                    .humidity(latestData.getHumidity())
                    .light(latestData.getLight())
                    .temperature(latestData.getTemperature())
                    .measuredTime(latestData.getMeasuredTime().toString())
                    .build();

            String jsonData = objectMapper.writeValueAsString(sensorDataDTO);

            if (sink.currentSubscriberCount() > 0) {
                Sinks.EmitResult result = sink.tryEmitNext("device/" + deviceId + ":" + jsonData);
                if (result.isSuccess()) {
                    log.info("Data sent successfully to device: {}", deviceId);
                } else {
                    log.warn("Failed to send data to device: {}. EmitResult: {}", deviceId, result);
                }
            } else {
                log.warn("No subscribers available, data not sent.");
            }
        } catch (Exception e) {
            log.error("Error fetching or sending data for device: {}", deviceId, e);
        }
    }

}
