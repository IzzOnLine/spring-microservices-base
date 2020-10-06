package it.izzonline.microservice.configuration.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {

	private RestApiErrorCode code;
	private List<String> errors;

	public ErrorResponseDto(RestApiErrorCode code) {
		this.code = code;
	}
}
