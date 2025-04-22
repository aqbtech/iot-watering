package com.se.iotwatering.repository;

import com.se.iotwatering.entity.AlertState;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AlertStateRepository extends JpaRepository<AlertState, Long> {
    Optional<AlertState> findBySensorIdAndField(String sensorId, String field);
}
