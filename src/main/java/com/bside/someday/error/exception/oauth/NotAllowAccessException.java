package com.bside.someday.error.exception.oauth;

import static com.bside.someday.error.dto.ErrorType.*;

import com.bside.someday.error.exception.BusinessException;

public class NotAllowAccessException extends BusinessException {

	private static final long serialVersionUID = -7304183691896008750L;

	public NotAllowAccessException() {
		super(NOT_ALLOW_ACCESS);
	}
}
