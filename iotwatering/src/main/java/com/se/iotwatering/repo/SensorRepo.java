package com.se.iotwatering.repo;

import com.se.iotwatering.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepo extends JpaRepository<Sensor, Long> {
}
