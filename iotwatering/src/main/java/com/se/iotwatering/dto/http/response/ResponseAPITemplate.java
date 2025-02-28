package com.se.iotwatering.dto.http.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseAPITemplate<T> {
	@Builder.Default
	private int code = 200;
	@Builder.Default
	private String message = "Success";
	private T result;
}
