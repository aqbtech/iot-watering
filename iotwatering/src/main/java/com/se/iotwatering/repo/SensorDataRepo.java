package com.se.iotwatering.repo;

import com.se.iotwatering.entity.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorDataRepo extends JpaRepository<SensorData, Long> {
}
