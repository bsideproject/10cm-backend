package com.bside.someday.error;

import static org.springframework.http.HttpStatus.*;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;

import com.bside.someday.error.exception.InvalidParameterException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.bside.someday.error.dto.ErrorDto;
import com.bside.someday.error.dto.ErrorType;
import com.bside.someday.error.exception.BusinessException;
import com.bside.someday.error.exception.oauth.TokenExpiredException;
import com.bside.someday.error.exception.oauth.NotAllowAccessException;
import com.bside.someday.error.exception.oauth.UnAuthorizedException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({NoSuchElementException.class})
	protected ResponseEntity<ErrorDto> handleNoSuchElementException(final BusinessException e,
																	 final HttpServletRequest request) {
		log.error("NoSuchElementException -> {}", e.toString());
		log.error("Request url -> {}", request.getRequestURL());
		return ResponseEntity
				.status(e.getErrorType().getStatus())
				.body(
						new ErrorDto(e.getErrorType())
				);
	}

	@ExceptionHandler({InvalidParameterException.class})
	protected ResponseEntity<ErrorDto> handleInvalidParameterException(final InvalidParameterException e,
																	final HttpServletRequest request) {
		log.error("InvalidParameterException -> {}", e.toString());
		log.error("Request url -> {}", request.getRequestURL());
		return ResponseEntity
				.status(e.getErrorType().getStatus())
				.body(
						new ErrorDto(e.getErrors().getAllErrors().get(0).getDefaultMessage()
								, e.getErrorType().getCode()
								, e.getErrorType().getStatus())
				);
	}
	@ExceptionHandler({TokenExpiredException.class, NotAllowAccessException.class,
		UnAuthorizedException.class})
	protected ResponseEntity<ErrorDto> handleAuthenticationException(final BusinessException e,
		final HttpServletRequest request) {
		log.error("AuthenticationException -> {}", e.toString());
		log.error("Request url -> {}", request.getRequestURL());
		return ResponseEntity
			.status(e.getErrorType().getStatus())
			.body(
				new ErrorDto(e.getErrorType())
			);
	}

	@ExceptionHandler(BusinessException.class)
	protected ResponseEntity<ErrorDto> handleBusinessException(final BusinessException e,
		final HttpServletRequest request) {
		log.error("BusinessException -> {} ", e.toString());
		log.error("Request url -> {} ", request.getRequestURL());
		return ResponseEntity
			.status(e.getErrorType().getStatus())
			.body(
				new ErrorDto(e.getErrorType())
			);
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorDto> handleException(final Exception e,
		final HttpServletRequest request) {
		var errorMessage =
			e.getCause().toString() + "\n" + e.getLocalizedMessage() + Arrays.toString(e.getStackTrace());
		log.error("Exception -> {} | {}", e.getMessage(), errorMessage);
		log.error("Request -> {} ", request.getRequestURL());
		return ResponseEntity
			.status(INTERNAL_SERVER_ERROR)
			.body(
				new ErrorDto(ErrorType.UNEXPECTED_ERROR)
			);
	}

}
