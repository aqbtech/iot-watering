package com.se.iotwatering.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "alert_state", uniqueConstraints = {@UniqueConstraint(columnNames = {"sensor_id", "field"})})
public class AlertState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sensor_id", nullable = false)
    private String sensorId;

    @Column(name = "field", nullable = false)
    private String field;

    @Column(name = "alerted", nullable = false)
    private boolean alerted;
}
