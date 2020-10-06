package it.izzonline.microservice.configuration.rest;

import static it.izzonline.microservice.configuration.dto.RestApiErrorCode.INTERNAL;
import static it.izzonline.microservice.configuration.dto.RestApiErrorCode.SECURITY;
import static it.izzonline.microservice.configuration.dto.RestApiErrorCode.VALIDATION;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ServerWebExchange;

import it.izzonline.microservice.configuration.dto.ErrorResponseDto;
import it.izzonline.microservice.configuration.dto.RestApiErrorCode;
import lombok.extern.log4j.Log4j2;

/**
 * Global exception handler to manage unhandled errors in the Rest layer
 * (Controllers)
 */
@ControllerAdvice
@Log4j2
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalErrorWebExceptionHandler {

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponseDto> accessDeniedException(AccessDeniedException exception, WebRequest request) {
		log.error(getErrorMessageUsingHttpRequest(request), exception);
		return buildErrorResponse(SECURITY, asList("Access denied"), FORBIDDEN);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponseDto> constraintViolationException(ConstraintViolationException exception,
			WebRequest request) {
		log.error(getErrorMessageUsingHttpRequest(request), exception);
		List<String> errorMessages = getConstraintViolationExceptionErrorMessages(exception); // HttpMessageNotReadableException
		return buildErrorResponse(VALIDATION, errorMessages, BAD_REQUEST);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponseDto> httpMessageNotReadableException(HttpMessageNotReadableException exception,
			WebRequest request) {
		log.error(getErrorMessageUsingHttpRequest(request), exception);
		return buildErrorResponse(VALIDATION, asList("The was a problem in the parameters of the current request"),
				BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponseDto> methodArgumentNotValidException(MethodArgumentNotValidException exception,
			WebRequest request) {
		log.error(getErrorMessageUsingHttpRequest(request), exception);
		List<String> errorMessages = getMethodArgumentNotValidExceptionErrorMessages(exception);
		return buildErrorResponse(VALIDATION, errorMessages, BAD_REQUEST);
	}

	@ExceptionHandler(Throwable.class)
	public ResponseEntity<ErrorResponseDto> throwable(Throwable exception, WebRequest request) {
		log.error(getErrorMessageUsingHttpRequest(request), exception);
		return buildErrorResponse(INTERNAL, asList("Internal error in the application"), INTERNAL_SERVER_ERROR);
	}

	/**
	 * Using the given {@link ServerWebExchange} builds a message with information
	 * about the Http request
	 *
	 * @param request {@link WebRequest} with the request information
	 *
	 * @return error message with Http request information
	 */
	private String getErrorMessageUsingHttpRequest(WebRequest request) {
		HttpServletRequest httpRequest = ((ServletWebRequest) request).getRequest();
		return format("There was an error trying to execute the request with: %s" + "Http method = %s %s " + "Uri = %s",
				System.lineSeparator(), httpRequest.getMethod(), System.lineSeparator(), httpRequest.getRequestURI());
	}

	/**
	 * Get the list of internal errors included in the given exception
	 *
	 * @param exception {@link MethodArgumentNotValidException} with the error
	 *                  information
	 *
	 * @return {@link List} of {@link String} with the error messages
	 */
	private List<String> getMethodArgumentNotValidExceptionErrorMessages(MethodArgumentNotValidException exception) {
		return exception
				.getBindingResult().getFieldErrors().stream().map(fe -> "Field error in object '" + fe.getObjectName()
						+ "' on field '" + fe.getField() + "' due to: " + fe.getDefaultMessage())
				.collect(Collectors.toList());
	}

	/**
	 * Get the list of internal errors included in the given exception
	 *
	 * @param exception {@link ConstraintViolationException} with the error
	 *                  information
	 *
	 * @return {@link List} of {@link String} with the error messages
	 */
	private List<String> getConstraintViolationExceptionErrorMessages(ConstraintViolationException exception) {
		return exception.getConstraintViolations().stream()
				.map(ce -> "Error in path '" + ce.getPropertyPath() + "' due to: " + ce.getMessage())
				.collect(Collectors.toList());
	}

	/**
	 * Builds the Http response related with an error, using the provided
	 * parameters.
	 *
	 * @param errorCode     {@link RestApiErrorCode} included in the response
	 * @param errorMessages {@link List} of error messages to include
	 * @param httpStatus    {@link HttpStatus} used in the Http response
	 *
	 * @return {@link ResponseEntity} of {@link ErrorResponseDto} with the suitable
	 *         Http response
	 */
	private ResponseEntity<ErrorResponseDto> buildErrorResponse(RestApiErrorCode errorCode, List<String> errorMessages,
			HttpStatus httpStatus) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(APPLICATION_JSON);

		ErrorResponseDto error = new ErrorResponseDto(errorCode, errorMessages);
		return new ResponseEntity<>(error, headers, httpStatus);
	}

}
