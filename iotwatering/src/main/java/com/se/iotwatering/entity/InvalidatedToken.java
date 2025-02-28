package com.se.iotwatering.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvalidatedToken {
	@Id
	private String id;
	private Date expiryTime;
}
