package com.bside.someday.error.exception.oauth;

import static com.bside.someday.error.dto.ErrorType.*;

import com.bside.someday.error.exception.BusinessException;

public class UnAuthorizedException extends BusinessException {

	private static final long serialVersionUID = 4452605022718082801L;

	public UnAuthorizedException() {
		super(UNAUTHORIZED);
	}
}
