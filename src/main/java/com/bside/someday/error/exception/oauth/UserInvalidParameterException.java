package com.bside.someday.error.exception.oauth;

import static com.bside.someday.error.dto.ErrorType.*;

import com.bside.someday.error.exception.BusinessException;

public class UserInvalidParameterException extends BusinessException {

	private static final long serialVersionUID = -3243426857479184719L;

	public UserInvalidParameterException() {
		super(USER_INVALID_PARAMETER);
	}

	public UserInvalidParameterException(String message) {
		super(message, USER_INVALID_PARAMETER);
	}
}
