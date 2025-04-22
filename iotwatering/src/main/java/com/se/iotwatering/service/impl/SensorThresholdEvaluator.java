package com.se.iotwatering.service.impl;

import com.se.iotwatering.entity.SensorData;
import com.se.iotwatering.entity.Configuration;
import com.se.iotwatering.entity.Sensor;
import com.se.iotwatering.service.ThresholdEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SensorThresholdEvaluator implements ThresholdEvaluator {
    private final ActionService actionService;

    @Override
    public void evaluate(SensorData data, Configuration config) {
        Sensor sensor = data.getSensor();
        double temp = Double.parseDouble(data.getTemperature());
        double light = Double.parseDouble(data.getLight());
        double soil = Double.parseDouble(data.getSoilMoisture());
        boolean auto = config.isAutoControlEnabled();
        String htmlContent = """
        <!DOCTYPE html>
        <html>
          <head> ... </head>
          <body>
            <div class="container">
              <div class="title">⚠️ Cảnh báo cảm biến vượt ngưỡng!</div>
              <div class="info">
                Trường: <strong>%s</strong><br/>
                Giá trị hiện tại: <strong>%s</strong><br/>
                Giá trị cấu hình: <strong>%s</strong>
              </div>
              <div class="footer">
                Vui lòng kiểm tra và xử lý sớm để đảm bảo hệ thống hoạt động an toàn.
              </div>
            </div>
          </body>
        </html>
        """;

        // Fan control
        if (temp > config.getTemperature()) {
            actionService.sendEmailOnce(sensor.getPureSensorId(), "temperature",
                    htmlContent.formatted("Nhiệt độ", temp, config.getTemperature()));
            if (auto) actionService.turnOnFan(sensor);
        } else {
            actionService.resetEmailAlert(sensor.getPureSensorId(), "temperature");
            if (auto) actionService.turnOffFan(sensor);
        }
        // Light control
        if (light < config.getLight()) {
            actionService.sendEmailOnce(sensor.getPureSensorId(), "light",
                    htmlContent.formatted("Ánh sáng", light, config.getLight()));
            if (auto) actionService.turnOnLight(sensor);
        } else {
            actionService.resetEmailAlert(sensor.getPureSensorId(), "light");
            if (auto) actionService.turnOffLight(sensor);
        }
        // Pump control
        if (soil < config.getSoilMoisture()) {
            actionService.sendEmailOnce(sensor.getPureSensorId(), "soilMoisture",
                    htmlContent.formatted("Độ ẩm đất", soil, config.getSoilMoisture()));
            if (auto) actionService.turnOnPump(sensor);
        } else {
            actionService.resetEmailAlert(sensor.getPureSensorId(), "soilMoisture");
            if (auto) actionService.turnOffPump(sensor);
        }
    }
}
