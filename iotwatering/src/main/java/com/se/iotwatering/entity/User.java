package com.se.iotwatering.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
	@Id
	private long userId;
	private String username;
	private String password;
	private String email;
	private String firstName;
	private String lastName;

	@OneToMany(mappedBy = "user")
	private List<IrrigationSchedule> schedules = new ArrayList<>();
}
