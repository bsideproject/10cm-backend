package com.bside.someday.error.exception;

import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import java.util.List;

import static com.bside.someday.error.dto.ErrorType.INVALID_PARAMETER;

public class InvalidParameterException extends BusinessException {

	private static final long serialVersionUID = 4880303669808514123L;

	private final Errors errors;

	public InvalidParameterException(Errors errors) {
		super(INVALID_PARAMETER);
		this.errors = errors;
	}

	public Errors getErrors() {
		return this.errors;
	}
}
