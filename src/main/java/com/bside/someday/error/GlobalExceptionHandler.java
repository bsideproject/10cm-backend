package com.bside.someday.error;

import static org.springframework.http.HttpStatus.*;

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
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.bside.someday.error.dto.ErrorDto;
import com.bside.someday.error.dto.ErrorType;
import com.bside.someday.error.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

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

	/**
	 * 바인드 예외 핸들링
	 */
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

	/**
	 * 파일 업로드 용량 초과 에러 핸들링
	 */
	@ExceptionHandler(MultipartException.class)
	protected ResponseEntity<ErrorDto> handleSizeLimitException(final Exception ex,
		final HttpServletRequest request) {

		if (ex instanceof MaxUploadSizeExceededException) {
			return ResponseEntity
				.status(BAD_REQUEST)
				.body(
					new ErrorDto(ErrorType.FILE_SIZE_LIMIT_EXCEEDED)
				);
		}

		return handleException(ex, request);
	}

	@ExceptionHandler(BusinessException.class)
	protected ResponseEntity<ErrorDto> handleBusinessException(final BusinessException ex,
		final HttpServletRequest request) {

		log.error("BusinessException -> {} ", ex.toString());
		log.error("Request url -> {} ", request.getRequestURL());
		return ResponseEntity
			.status(ex.getErrorType().getStatus())
			.body(
				new ErrorDto(ex.getErrorType())
			);
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorDto> handleException(final Exception ex,
		final HttpServletRequest request) {
		log.error("Exception -> {}", ex.getMessage());
		log.error("Request -> {} ", request.getRequestURL());
		return ResponseEntity
			.status(INTERNAL_SERVER_ERROR)
			.body(
				new ErrorDto(ErrorType.UNEXPECTED_ERROR)
			);
	}

}
