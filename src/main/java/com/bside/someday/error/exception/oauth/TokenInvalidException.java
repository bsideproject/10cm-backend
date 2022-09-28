package com.bside.someday.error.exception.oauth;

import static com.bside.someday.error.dto.ErrorType.*;

import com.bside.someday.error.exception.BusinessException;

public class TokenInvalidException extends BusinessException {

	private static final long serialVersionUID = 8416612825200097596L;

	public TokenInvalidException() {
		super(TOKEN_INVALID);
	}

}
