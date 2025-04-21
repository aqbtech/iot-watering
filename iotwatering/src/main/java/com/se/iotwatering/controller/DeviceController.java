package com.se.iotwatering.controller;

import com.se.iotwatering.dto.DeviceInfo;
import com.se.iotwatering.dto.SensorData;
import com.se.iotwatering.dto.SensorDetailResponse;
import com.se.iotwatering.dto.http.response.ResponseAPITemplate;
import com.se.iotwatering.service.impl.DataPollingService;
import com.se.iotwatering.service.impl.DeviceControllerService;
import com.se.iotwatering.service.impl.TelemetryService;
import com.se.iotwatering.service.impl.WebSocketClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;

@RestController
@RequestMapping("/device/v1")
@RequiredArgsConstructor
@Getter
public class DeviceController {
    private final DeviceControllerService deviceControllerService;
    private final TelemetryService telemetryService;
    private final DataPollingService dataPollingService;
    @Autowired
    private WebSocketClient wsClient;

    @GetMapping("/subscribe")
    public ResponseAPITemplate<?> subDevice(@RequestParam("dvcId") String deviceId) {
        try {
            wsClient.subscribeToDevice(deviceId);
            String topic = "device/" + deviceId;
            return ResponseAPITemplate.<String>builder()
                    .result(topic)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/list-device")
    public ResponseAPITemplate<Page<DeviceInfo>> listDevice(@AuthenticationPrincipal Jwt jwt,
                                                            @RequestParam("page") int page,
                                                            @RequestParam("size") int size) {
        //TODO: Get list device
        Page<DeviceInfo> dvcs = deviceControllerService.listDevice(page, size);
        return ResponseAPITemplate.<Page<DeviceInfo>>builder()
                .result(dvcs)
                .build();
    }

    @PostMapping("/trigger")
    public ResponseAPITemplate<?> triggerDevice(@RequestParam("dvcId") long deviceId,
                                                @RequestParam(value = "duration", required = false) String duration) {
        return ResponseAPITemplate.<String>builder()
                .result(deviceControllerService.triggerPump(deviceId))
                .build();
    }

    @GetMapping("/detail")
    public ResponseAPITemplate<SensorDetailResponse> triggerDevice(@RequestParam("dvcId") long deviceId) {
        return ResponseAPITemplate.<SensorDetailResponse>builder()
                .result(deviceControllerService.getDeviceById(deviceId))
                .build();
    }

    @GetMapping("/history")
    public ResponseAPITemplate<Page<SensorData>> getHistory(@RequestParam("dvcId") long deviceId,
                                                            @RequestParam(value = "from", required = false) String from,
                                                            @RequestParam(value = "to", required = false) String to,
                                                            @RequestParam("page") int page,
                                                            @RequestParam("size") int size) {
        Page<SensorData> sensorData = telemetryService.getHistory(deviceId, from, to, page, size);
        return ResponseAPITemplate.<Page<SensorData>>builder()
                .result(sensorData)
                .build();
    }

    @PostMapping("/threshold-config")
    public ResponseAPITemplate<?> configThreshold(@RequestParam("dvcId") String deviceId,
                                                  @RequestParam("threshold") double thresholdReq) {
        //TODO: Config threshold
        return new ResponseAPITemplate<>();
    }

    @GetMapping("/latest-telemetry")
    public ResponseAPITemplate<?> getLatestTelemetry(@RequestParam("dvcId") long deviceId) {
        SensorData sensorData = telemetryService.getLatestTelemetry(deviceId);
        return ResponseAPITemplate.<SensorData>builder()
                .result(sensorData)
                .build();
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamDeviceEvents(@RequestParam String deviceId) {
        System.out.println("🔔 Client subscribed for device: " + deviceId);
        dataPollingService.setActiveDevice(deviceId);
        dataPollingService.startFetching();
        return dataPollingService.getSink().asFlux()
                .doOnSubscribe(s -> System.out.println("New subscriber added!"))
                .doOnNext(event -> System.out.println("Sending event: " + event))
                .filter(event -> event.startsWith("device/" + deviceId))
                .map(event -> event.replace("device/" + deviceId + ":", ""))
                .onErrorComplete()
                .delayElements(Duration.ofSeconds(1))
                .doFinally(signalType -> {
                    System.out.println("Client unsubscribed from device: " + deviceId);
                    if (dataPollingService.getSink().currentSubscriberCount() == 0) {
                        System.out.println("No more subscribers, resetting sink...");
                        dataPollingService.stopFetching();
                        dataPollingService.setActiveDevice(null);
                        dataPollingService.resetSink();
                    }
                });
    }

    @PostMapping("/{deviceId}/send")
    public void sendEvent(@PathVariable String deviceId) {
//        if (dataPollingService.getActiveDevices().size() == 1) {
//            System.out.println(dataPollingService.getActiveDevices().size());
//        } else {
//            System.out.println(dataPollingService.getActiveDevices().size());
//        }
        System.out.println(dataPollingService.getActiveDevice());
        if (dataPollingService.getSink().currentSubscriberCount() == 1) {
            System.out.println(dataPollingService.getSink().currentSubscriberCount());
        } else {
            System.out.println(dataPollingService.getSink().currentSubscriberCount());
        }
    }
}
