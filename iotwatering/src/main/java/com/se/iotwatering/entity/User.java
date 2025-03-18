package com.se.iotwatering.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long userId;
	@Column(nullable = false, unique = true)
	private String username;
	private String password;
	private String email;
	private String firstName;
	private String lastName;
	private String phone;
	private LocalDate dob;

	@OneToMany(mappedBy = "user")
	private List<IrrigationSchedule> schedules;
	@ManyToMany(mappedBy = "users")
	private List<Sensor> sensors;

	// Constructor không đối số phải là protected để JPA có thể sử dụng.
	protected User() {
		this.schedules = new ArrayList<>();
	}

	// Constructor private dùng cho Builder.
	private User(Builder builder) {
		this.userId = builder.userId;
		this.username = builder.username;
		this.password = builder.password;
		this.email = builder.email;
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
		this.dob = builder.dob;
		this.phone = builder.phone;
		this.schedules = builder.schedules != null ? builder.schedules : new ArrayList<>();
	}

	// Phương thức tĩnh tạo Builder
	public static Builder builder() {
		return new Builder();
	}

	// Inner static Builder class
	public static class Builder {
		private long userId;
		private String username;
		private String password;
		private String email;
		private String firstName;
		private String lastName;
		private String phone;
		private LocalDate dob;
		private List<IrrigationSchedule> schedules;

		public Builder dob(LocalDate dob) {
			this.dob = dob;
			return this;
		}

		public Builder phone(String phone) {
			this.phone = phone;
			return this;
		}

		public Builder userId(long userId) {
			this.userId = userId;
			return this;
		}

		public Builder username(String username) {
			this.username = username;
			return this;
		}

		public Builder password(String password) {
			this.password = password;
			return this;
		}

		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public Builder firstName(String firstName) {
			this.firstName = firstName;
			return this;
		}

		public Builder lastName(String lastName) {
			this.lastName = lastName;
			return this;
		}

		public Builder schedules(List<IrrigationSchedule> schedules) {
			this.schedules = schedules;
			return this;
		}

		public User build() {
			return new User(this);
		}
	}
}

