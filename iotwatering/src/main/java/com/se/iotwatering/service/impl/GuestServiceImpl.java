package com.se.iotwatering.service.impl;


import com.se.iotwatering.constant.SystemConstant;
import com.se.iotwatering.dto.http.request.UserRegister;
import com.se.iotwatering.dto.http.response.MinimalUserProfile;
import com.se.iotwatering.entity.User;
import com.se.iotwatering.exception.BaseErrorCode;
import com.se.iotwatering.exception.UserErrorCode;
import com.se.iotwatering.exception.WebServerException;
import com.se.iotwatering.repo.UserRepository;
import com.se.iotwatering.service.GuestService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class GuestServiceImpl implements GuestService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional(value = Transactional.TxType.REQUIRES_NEW)
	public MinimalUserProfile register(UserRegister userRegister) {
		// Tạo đối tượng User mới với dữ liệu từ userRegister
		User newUser = User.builder()
				.username(userRegister.getUsername())
				.password(passwordEncoder.encode(userRegister.getPassword()))
				.email(userRegister.getEmail())
				.firstName(userRegister.getFirstName())
				.lastName(userRegister.getLastName())
				.phone(userRegister.getPhone())
				.dob(userRegister.getDob())
				.build();
		try {
			// Lưu và flush đối tượng vào database
			userRepository.saveAndFlush(newUser);
		} catch (DataIntegrityViolationException e) {
			throw new WebServerException(UserErrorCode.USER_EXISTED);
		} catch (Exception e) {
			log.error("Error when registering new user: {}", e.getMessage());
			throw new WebServerException(BaseErrorCode.UNKNOWN_ERROR);
		}
		// Trả về thông tin profile tối giản của user mới
		return MinimalUserProfile.builder()
				.username(newUser.getUsername())
				// Nếu MinimalUserProfile có thuộc tính role, có thể set role theo hệ thống
				.role(SystemConstant.ROLE_USER)
				.build();
	}
}

