package com.bside.someday.error.exception;

import static com.bside.someday.error.dto.ErrorType.NO_SUCH_ELEMENT;

public class NoSuchElementException extends BusinessException {

	private static final long serialVersionUID = -7648019793806396785L;

	public NoSuchElementException() {
		super(NO_SUCH_ELEMENT);
	}
}
