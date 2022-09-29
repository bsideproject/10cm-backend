package com.bside.someday.error.exception.oauth;

import static com.bside.someday.error.dto.ErrorType.*;

import com.bside.someday.error.dto.ErrorType;
import com.bside.someday.error.exception.BusinessException;

public class UserNotFoundException extends BusinessException {

	public UserNotFoundException() {
		super(USER_NOT_FOUND);
	}
}
