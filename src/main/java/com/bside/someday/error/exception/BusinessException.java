
package com.bside.someday.error.exception;

import com.bside.someday.error.dto.ErrorType;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 2696951067557782762L;
	private final ErrorType errorType;

	public BusinessException(ErrorType errorType) {
		super(errorType.getMessage());
		this.errorType = errorType;
	}

	public BusinessException(String message, ErrorType errorType) {
		super(message);
		this.errorType = errorType;
	}
}
