package com.se.iotwatering.repo;

import com.se.iotwatering.entity.Configuration;
import com.se.iotwatering.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {
    Optional<Configuration> findBySensor(Sensor sensor);
}
