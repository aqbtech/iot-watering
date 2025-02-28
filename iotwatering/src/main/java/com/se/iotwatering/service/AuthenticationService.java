package com.se.iotwatering.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import com.se.iotwatering.constant.SystemConstant;
import com.se.iotwatering.dto.http.request.AuthenticationRequest;
import com.se.iotwatering.dto.http.request.IntrospectRequest;
import com.se.iotwatering.dto.http.request.LogoutRequest;
import com.se.iotwatering.dto.http.request.RefreshRequest;
import com.se.iotwatering.dto.http.response.AuthenticationResponse;
import com.se.iotwatering.dto.http.response.IntrospectResponse;
import com.se.iotwatering.entity.InvalidatedToken;
import com.se.iotwatering.exception.ErrorCode;
import com.se.iotwatering.exception.WebServerException;
import com.se.iotwatering.repo.InvalidatedTokenRepository;
import com.se.iotwatering.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true)
public class AuthenticationService implements AuthenticationProvider {
	UserRepository userRepository;
	InvalidatedTokenRepository invalidatedTokenRepository;
	@NonFinal
	@Value("${jwt.signerKey}")
	protected String SIGNER_KEY;
	@NonFinal
	@Value("${jwt.valid-duration}")
	protected long VALID_DURATION;
	@NonFinal
	@Value("${jwt.refreshable-duration}")
	protected long REFRESHABLE_DURATION;

	public IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException {
		var token = request.getToken();
		boolean isValid = true;
		try {
			verifyToken(token, false);
		} catch (WebServerException e) {
			isValid = false;
		}
		return IntrospectResponse.builder().valid(isValid).build();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
//		var user = userRepository.findUserWithRoleByUsername(request.getUsername());
//		if (user.isEmpty()) {
//			throw new WebServerException(ErrorCode.UNAUTHENTICATED);
//		}
//		boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getFirst()[1].toString());
//		if (!authenticated) {
//			throw new WebServerException(ErrorCode.UNAUTHENTICATED);
//		}
//		var token = generateToken(user.getFirst()[0].toString(), user.getFirst()[2].toString());
		var user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new WebServerException(ErrorCode.USER_NOT_FOUND));
		var token = generateToken(user.getUsername(), SystemConstant.ROLE_USER);
		return AuthenticationResponse.builder()
				.token(token.get("token"))
				.authenticatedToken(true)
				.role(token.get("role"))
				.build();
	}

	public void logout(LogoutRequest request) throws ParseException, JOSEException {
		try {
			var signedJWT = verifyToken(request.getToken(), true);
			String jti = signedJWT.getJWTClaimsSet().getJWTID();
			Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
			// save invalidated token to db
			InvalidatedToken invalidatedToken = InvalidatedToken.builder()
					.id(jti)
					.expiryTime(expirationTime)
					.build();
			invalidatedTokenRepository.save(invalidatedToken);
		} catch (WebServerException e) {
			log.info("Token already invalidated");
		}
	}

	public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
		var signedJWT = verifyToken(request.getRefreshToken(), true);

		var jti = signedJWT.getJWTClaimsSet().getJWTID();
		Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

		// save old token to invalidated token
		InvalidatedToken invalidatedToken
				= InvalidatedToken.builder().id(jti).expiryTime(expirationTime).build();
		invalidatedTokenRepository.save(invalidatedToken);

		var username = signedJWT.getJWTClaimsSet().getSubject();
		if (!userRepository.existsByUsername(username))
			throw new WebServerException(ErrorCode.USER_NOT_FOUND);
		var token = generateToken(username, signedJWT.getJWTClaimsSet().getClaim("authorities").toString());
		return AuthenticationResponse.builder()
				.token(token.get("token"))
				.authenticatedToken(true)
				.role(token.get("role"))
				.build();
	}

	private Map<String, String> generateToken(String username, String... authorities) {
		JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS512).build();
		String scope = buildScope(authorities);
		JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
				.subject(username)
				.issuer("hcmut.se")
				.issueTime(new Date())
				.expirationTime(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS)
						.toEpochMilli()))
				.jwtID((UUID.randomUUID()).toString())
				.claim("authorities", scope)
				.build();

		Payload payload = new Payload(jwtClaimsSet.toJSONObject());
		JWSObject jwsObject = new JWSObject(header, payload);

		try {
			jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
			return Map.of("token", jwsObject.serialize(), "role", scope);
		} catch (JOSEException e) {
			log.error("Error signing token", e);
			throw new RuntimeException(e);
		}
	}

	private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
		JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
		SignedJWT signedJWT = SignedJWT.parse(token);
		Date expirationTime = (isRefresh)
				? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
				.toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
				: signedJWT.getJWTClaimsSet().getExpirationTime();

		var verified = signedJWT.verify(verifier);
		if (!verified && expirationTime.after(new Date())) {
			throw new WebServerException(ErrorCode.UNAUTHENTICATED);
		}
		// check if token is invalidated
		if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
			throw new WebServerException(ErrorCode.UNAUTHENTICATED);
//			 throw new WebServerException(ErrorCode.INVALID_TOKEN);
		}
		return signedJWT;
	}

	private String buildScope(Object[] result) {
		return new StringJoiner(" ")
				.add(result[0].toString())
				.toString();
	}
}
