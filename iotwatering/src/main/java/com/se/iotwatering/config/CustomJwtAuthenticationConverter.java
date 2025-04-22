package com.se.iotwatering.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CustomJwtAuthenticationConverter  implements Converter<Jwt, AbstractAuthenticationToken> {
	@Override
	public AbstractAuthenticationToken convert(Jwt jwt) {
		// Nếu "authorities" tồn tại, chuyển đổi nó thành danh sách
		List<String> authoritiesClaim = jwt.getClaimAsStringList("authorities");

		List<GrantedAuthority> authorities = authoritiesClaim != null
				? authoritiesClaim.stream()
				.map(role -> "ROLE_" + role)
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList())
				: Collections.emptyList();


		// Tạo JwtAuthenticationConverter với các quyền đã ánh xạ
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthorities -> authorities);

		return jwtAuthenticationConverter.convert(jwt);
	}
}