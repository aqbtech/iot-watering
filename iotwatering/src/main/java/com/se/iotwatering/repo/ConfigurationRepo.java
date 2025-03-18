package com.se.iotwatering.repo;


import com.se.iotwatering.entity.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigurationRepo extends JpaRepository<Configuration, Long> {
}
