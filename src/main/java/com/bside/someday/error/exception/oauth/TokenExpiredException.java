package com.bside.someday.error.exception.oauth;

import static com.bside.someday.error.dto.ErrorType.*;

import com.bside.someday.error.exception.BusinessException;

public class TokenExpiredException extends BusinessException {

	private static final long serialVersionUID = -7471994487377258L;

	public TokenExpiredException() {
		super(TOKEN_EXPIRED);
	}
}
