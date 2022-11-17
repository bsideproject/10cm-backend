package com.bside.someday.error;

import static org.springframework.http.HttpStatus.*;

import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;

import com.bside.someday.error.exception.InvalidParameterException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
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

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
		HttpHeaders headers, HttpStatus status, WebRequest request) {

		if (!ex.getBindingResult().hasErrors()) {
			return super.handleMethodArgumentNotValid(ex, headers, status, request);
		}

		return ResponseEntity
			.status(BAD_REQUEST)
			.body(
				new ErrorDto(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage(), "INVALID", BAD_REQUEST)
			);
	}

	@Override
	protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
		WebRequest request) {
		if (!ex.getBindingResult().hasErrors()) {
			return super.handleBindException(ex, headers, status, request);
		}

		return ResponseEntity
			.status(BAD_REQUEST)
			.body(
				new ErrorDto(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage(), "INVALID", BAD_REQUEST)
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
		log.error("Exception -> {}", e.getMessage());
		log.error("Request -> {} ", request.getRequestURL());
		return ResponseEntity
			.status(INTERNAL_SERVER_ERROR)
			.body(
				new ErrorDto(ErrorType.UNEXPECTED_ERROR)
			);
	}

}
