package com.se.iotwatering;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IotwateringApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotwateringApplication.class, args);
    }

}
