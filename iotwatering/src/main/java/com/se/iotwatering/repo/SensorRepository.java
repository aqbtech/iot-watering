package com.se.iotwatering.repo;

import com.se.iotwatering.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    
    /**
     * Find a sensor by its device ID
     * @param pureSensorId The device ID
     * @return Optional containing the sensor if found
     */
    Optional<Sensor> findByPureSensorId(String pureSensorId);
    
    /**
     * Check if a sensor with the given device ID exists
     * @param pureSensorId The device ID
     * @return True if a sensor with the given device ID exists
     */
    boolean existsByPureSensorId(String pureSensorId);
}
